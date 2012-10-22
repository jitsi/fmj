java -classpath fmj.jar:lib/jdom.jar:lib/lti-civil-no_s_w_t.jar:lib/jl1.0.jar:lib/tritonus_share.jar:lib/mp3spi1.9.4.jar:lib/jorbis-0.0.15.jar:lib/jogg-0.0.7.jar:lib/vorbisspi1.0.2.jar:lib/jspeex.jar:lib/jna.jar:lib/ffmpeg-java.jar:lib/theora-java.jar:lib/jheora-patch.jar -Djava.library.path="native/macosx-universal" -Dcom.apple.mrj.application.apple.menu.about.name="FMJ Studio" net.sf.fmj.apps.transcode.FmjTranscode $1 $2 $3 $4 %5 


