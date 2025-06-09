# /usr/bin/env sh
export DISPLAY=:0
[[ -s "/home/tk/.sdkman/bin/sdkman-init.sh" ]] && source "/home/tk/.sdkman/bin/sdkman-init.sh"
# sdk use java 22.0.2.fx-librca
sdk use java 11.0.22.fx-librca
java -cp ./open2samepdf.jar com.metait.open2samepdf.Open2SamePdfApplication $*
