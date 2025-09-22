package raoni.exc_26_09_2025;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import javax.sound.sampled.AudioFormat;

public class ConsoleMultiTrackMixer {

    private final Map<String, AudioPlayer> players = new HashMap<>();
    private final String[] instruments = {
            "AcousticGuitar",
            "Bass",
            "DrumKit",
            "EletricGuitar",
            "Harp",
            "Lv",
            "Oboe",
            "Piano",
            "StringsSec",
            "SynthKeys"};
    private final String basePath = "C:\\Users\\robso\\Music\\abba\\";

    public static void main(String[] args) {
        new ConsoleMultiTrackMixer().run();
    }

    public void run() {
        try {
            AudioFormat format = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED, 44100.0f, 16, 2, 4, 44100.0f, false
            );

            for (String instrument : instruments) {
                players.put(instrument, new AudioPlayer(basePath + instrument + ".wav", format));
            }

            System.out.println("Mixer do Console. Digite o nome do instrumento para ligar/desligar.");
            System.out.println("Instrumentos: " + String.join(", ", instruments));
            System.out.println("Comandos: 'play', 'stop', 'sair' ou o nome do instrumento.");

            Scanner scanner = new Scanner(System.in);
            while (true) {
                String input = scanner.nextLine().trim();

                if (input.equalsIgnoreCase("sair")) {
                    break;
                }

                if (input.equalsIgnoreCase("play")) {
                    players.values().forEach(AudioPlayer::play);
                    System.out.println("Reprodução iniciada para todas as faixas.");
                } else if (input.equalsIgnoreCase("stop")) {
                    players.values().forEach(AudioPlayer::stop);
                    System.out.println("Reprodução parada para todas as faixas.");
                } else if (players.containsKey(input)) {
                    players.get(input).toggleMute();
                    boolean isMuted = players.get(input).isMuted.get();
                    System.out.println("Faixa " + input + " foi " + (isMuted ? "mutada." : "desmutada."));
                } else {
                    System.out.println("Comando inválido.");
                }
            }

            players.values().forEach(AudioPlayer::stop);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}