# devoxx-video-linker

Matching the conference talks from [Devoxx 2015](http://cfp.devoxx.be/2015/index.html) with the uploaded
YouTube videos in the [Devoxx 2015 channel](https://www.youtube.com/channel/UCCBVCTuk6uJrN3iFV_3vurg).

It is a small script which simply matches on the conference title, note that since the YouTube video
titles are not completely consistent with the talk titles, I decided to fix this manually in the JSON
representation (see resources/youtube.json).

## Installation

The script is based on Clojure and therefore is executed in your favorite JVM at hand.
It requires leiningen to be installed on your computer for compiling and executing the script.


## Usage

From the command-line

    $ lein run


## Notes about the static resources

To retrieve the list of all accepted talks at the conference the [Devoxx BE REST API](http://cfp.devoxx.be/api) was used.
More information about talks can be queried via:

    curl http://cfp.devoxx.be/api/conferences/DV15/talks
    curl http://cfp.devoxx.be/api/conferences/DV15/talks/{talk-id}

The talks are mirrored in a dedicated PostgreSQL database for voting purposes and can be retrieved with:

    curl https://api-voting.devoxx.com/DV15/talk/{talk-id}

The list of available YouTube playlist items in the channel was retrieved via:

    curl https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=50&playlistId=UUCBVCTuk6uJrN3iFV_3vurg&key={YOUR_API_KEY}


## License

Copyright © 2015 Niko Schmuck

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
