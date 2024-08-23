package gh2;

import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;
import deque.ArrayDeque;

public class DrumHero {
    public static final double CONCERT_A = 440.0;
    public static final String KEYBOARD = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";

    public static void main(String[] args) {
        ArrayDeque<Drum> strings = new ArrayDeque<>();
        for (int i = 0; i < KEYBOARD.length(); i++) {
            double frequency = CONCERT_A - i * 11;
            Drum string = new Drum(frequency);
            strings.addLast(string);
        }

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                int index = KEYBOARD.indexOf(key);
                if (index >= 0) {
                    strings.get(index).pluck();
                }
            }

            double sample = 0.0;
            for (int i = 0; i < KEYBOARD.length(); i++) {
                sample += strings.get(i).sample();
            }

            StdAudio.play(sample);

            for (int i = 0; i < KEYBOARD.length(); i++) {
                strings.get(i).tic();
            }
        }
    }
}
