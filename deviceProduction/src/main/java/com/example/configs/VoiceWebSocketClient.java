package com.example.configs;

import lombok.*;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.stereotype.Service;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class VoiceWebSocketClient extends WebSocketClient {

    private TargetDataLine microphone;
    private SourceDataLine speakers;

    public VoiceWebSocketClient(String room) {
        super(URI.create("ws://193.168.49.199:8080/voice/" + room));
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Подключено к серверу");
        startCapturingAudio();
    }

    @Override
    public void onMessage(String s) {
        System.out.println(s);
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        playAudio(bytes.array());
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Соединение закрыто");
        stopAudio();
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    private void startCapturingAudio() {
        new Thread(() -> {
            try {
                AudioFormat format = getAudioFormat();
                DataLine.Info micInfo = new DataLine.Info(TargetDataLine.class, format);

                // Открытие микрофона
                microphone = (TargetDataLine) AudioSystem.getLine(micInfo);
                microphone.open(format);
                microphone.start();

                byte[] buffer = new byte[4096];
                while (isOpen()) {
                    int bytesRead = microphone.read(buffer, 0, buffer.length);
                    if (bytesRead > 0) {
                        // Сжатие данных перед отправкой через WebSocket
                        byte[] compressedData = compress(buffer, 0, bytesRead);

                        // Воспроизведение записанного аудио через динамики
                        //speakers.write(buffer, 0, bytesRead);

                        // Отправка сжатых данных
                        send(ByteBuffer.wrap(compressedData));
                        System.out.println("Отправлено сжатых данных: " + compressedData.length + " байт.");
                    }
                }

                microphone.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    private byte[] compress(byte[] data, int offset, int length) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
            gzipOutputStream.write(data, offset, length);
        }
        return byteArrayOutputStream.toByteArray();
    }
    private byte[] decompress(byte[] compressedData) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(compressedData))) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = gzipInputStream.read(buffer)) > 0) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

    private void playAudio(byte[] compressedData) {
        try {
            // Разжимаем сжатые данные
            byte[] decompressedData = decompress(compressedData);

            AudioFormat format = getAudioFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

            if (speakers == null) {
                speakers = (SourceDataLine) AudioSystem.getLine(info);
                speakers.open(format);
                speakers.start();
            }

            speakers.write(decompressedData, 0, decompressedData.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopAudio() {
        if (microphone != null) {
            microphone.stop();
            microphone.close();
        }
        if (speakers != null) {
            speakers.stop();
            speakers.close();
        }
    }

    private AudioFormat getAudioFormat() {
        float sampleRate = 32000.0F;
        int sampleSizeInBits = 16;
        int channels = 2; // Моно
        boolean signed = true;
        boolean bigEndian = false;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }
}
