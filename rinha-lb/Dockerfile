FROM ghcr.io/graalvm/native-image-community:21 as graalvm

COPY . /home/app/rinhalb
WORKDIR /home/app/rinhalb

RUN native-image --static  \
    --no-fallback  \
    -cp build/libs/rinhalb-1.0.jar io.rinha.RinhaLBKt

FROM frolvlad/alpine-glibc
EXPOSE 9999
COPY --from=graalvm /home/app/rinhalb/io.rinha.rinhalbkt /app/rinhalb
ENTRYPOINT ["/app/rinhalb"]