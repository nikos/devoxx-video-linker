(ns devoxx-video-linker.core
  (:require [cheshire.core :as json]
            [clj-http.client :as client])
  (:gen-class))

(defn read-youtube-videos
  "Returns array of (accepted) talks."
  []
  (json/decode (slurp "resources/youtube.json")))

(defn get-video
  "Returns videos which title starts with the given 'title' string."
  [videos title]
  (filter #(.startsWith (get-in % ["snippet" "title"]) title) videos))

(defn read-talks
  "Returns array of (accepted) talks."
  []
  (let [talks-raw (json/decode (slurp "resources/talks.json"))]
    (get-in talks-raw ["talks" "accepted"])))


(defn -main
  "Merge youtube video information with devoxx talks."
  [& args]
  (let [videos (read-youtube-videos)
        talks (read-talks)
        t2v (map #(assoc {} :talk %, :videos (get-video videos (% "title"))) talks)
        t2v-with-videos (remove #(empty? (:videos %)) t2v)
        t2v-without-videos (remove #(not (empty? (:videos %))) t2v)]

    ;; ~~
    (printf "%d youtube videos\n" (count videos))
    (printf "%d matched talks with videos ...\n\n" (count t2v-with-videos))
    (doseq [entry t2v-with-videos]
      (let [talkId ((:talk entry) "id")
            videoId (get-in (first (:videos entry)) ["snippet" "resourceId" "videoId"])
            youtubeURL (format "https://www.youtube.com/watch?v=%s" videoId)]
        ;;(printf "Talk ID:%s, Video: %s\n" talkId youtubeURL)
        ;; TODO: Once https://bitbucket.org/nschmuck/devoxx-vote-api gets merged and deployed, we should be able to
        (client/put "http://requestb.in/1b8ha2t1" {:form-params {:talkId talkId :youtubeURL youtubeURL}})
        ;;(client/put (format "https://api-voting.devoxx.com/DV15/talk/%s/youtubeURL" talkId) {:form-params {:youtubeURL youtubeURL}})
        ))

    ;; ~~
    (println)
    (printf "%d unmatched talks without video ...\n\n" (count t2v-without-videos))
    (doseq [entry t2v-without-videos]
      (printf " * [%6s] '%s' (%s)\n" (get-in (:talk entry) ["talkType" "id"]) ((:talk entry) "title") ((:talk entry) "id")))
    ))