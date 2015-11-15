(ns devoxx-video-linker.core
  (:require [cheshire.core :as json])
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
      (printf "Talk ID:%s, Video: %s\n" ((:talk entry) "id") (get-in (first (:videos entry)) ["snippet" "resourceId" "videoId"])))

    ;; ~~
    (println)
    (printf "%d unmatched talks without video ...\n\n" (count t2v-without-videos))
    (doseq [entry t2v-without-videos]
      (printf " * [%6s] '%s' (%s)\n" (get-in (:talk entry) ["talkType" "id"]) ((:talk entry) "title") ((:talk entry) "id") ))

    ))