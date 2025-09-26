package raoni.exc_26_09_2025;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
//boolean que atomico que so muda de estado de forma atomica(sem risco de varias threads a modificarem ao mesmo tempo)

public class AudioPlayer implements Runnable {

    private final String filePath;
    private final AudioFormat format;//biblioteca do java para trabalhar o formato de audio(configurar o formato do audio que receberemos)
    private final AtomicBoolean isPlaying = new AtomicBoolean(false);
    AtomicBoolean isMuted = new AtomicBoolean(false);
    private final AtomicBoolean isPaused = new AtomicBoolean(false);

    public AudioPlayer(String filePath, AudioFormat format) {
        this.filePath = filePath;
        this.format = format;
    }

    public void play() {
        if (!isPlaying.getAndSet(true)) {
            new Thread(this).start();
        }
    }
    public void pause() {
        isPaused.set(true);
    }

    public void resume() {
        isPaused.set(false);
    }

    public void stop() {
        if (isPlaying.getAndSet(false)) {
            // A thread vai parar naturalmente quando o loop for interrompido
        }
    }

    public void toggleMute() {
        isMuted.set(!isMuted.get());
    }

    @Override
    public void run() {
        // --- AQUI É ONDE VEMOS A THREAD ---
        System.out.println("Iniciando a reprodução da faixa " + filePath + " na thread: " + Thread.currentThread().getName());
        //--********************************--//

        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(new File(filePath));
            AudioInputStream convertedStream = AudioSystem.getAudioInputStream(format, stream);
            SourceDataLine line = AudioSystem.getSourceDataLine(format);
            line.open(format);
            line.start();

            byte[] buffer = new byte[line.getBufferSize() / 2];
            int bytesRead;

            while (isPlaying.get()) {
                if (isMuted.get()||isPaused.get()) {
                    Thread.sleep(100);
                    continue;
                }

                if ((bytesRead = convertedStream.read(buffer, 0, buffer.length)) != -1) {
                    if (bytesRead > 0) {
                        line.write(buffer, 0, bytesRead);
                    }
                } else {
                    stream.close();
                    stream = AudioSystem.getAudioInputStream(new File(filePath));
                    convertedStream = AudioSystem.getAudioInputStream(format, stream);
                }
            }

            line.drain();
            line.close();
            convertedStream.close();
            stream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}