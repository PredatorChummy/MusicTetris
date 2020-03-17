import processing.core.PApplet;
import processing.core.PFont;
import processing.data.FloatDict;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ddf.minim.*;
import ddf.minim.signals.*;
import ddf.minim.analysis.*;
import ddf.minim.effects.*;


public class MySketch extends PApplet {

    public static void main(String[] args) {
        String[] processingArgs = {"MySketch"};
        MySketch mySketch = new MySketch();
        PApplet.runSketch(processingArgs, mySketch);
    }

    int gameScreen = 0;
    int score = 0;
    int Round = 1;
    int currentSong = 0;
    int ending_round = 10;
    int no_of_plates = 5;
    String savedName;
    Drop[] plates = new Drop[no_of_plates];

    Minim minim;
    AudioPlayer[] song = new AudioPlayer[10];
    FFT fft;

    public void settings() {
        size(750, 750);smooth();
    }

    public void setup() {
        PFont f = createFont("Arial", 16, true);
        textFont(f, 12);
        rain(Round);
        minim = new Minim(this);

        song[0] = minim.loadFile("01 Radioactive.mp3", 2048);
        song[1] = minim.loadFile("01 Radioactive.mp3", 2048);
        song[2] = minim.loadFile("01 Radioactive.mp3", 2048);
        song[3] = minim.loadFile("01 Radioactive.mp3", 2048);
        song[4] = minim.loadFile("01 Radioactive.mp3", 2048);
        song[5] = minim.loadFile("01 Radioactive.mp3", 2048);
        song[6] = minim.loadFile("01 Radioactive.mp3", 2048);
        song[7] = minim.loadFile("01 Radioactive.mp3", 2048);
        song[8] = minim.loadFile("01 Radioactive.mp3", 2048);
        song[9] = minim.loadFile("01 Radioactive.mp3", 2048);
    }

    public void draw() {
        if (gameScreen == 0) {
            initScreen();
        } else if (gameScreen == 1) {
            gameScreen();
        } else if (gameScreen == 2) {
            gameOverScreen();
        }
    }

    public void mousePressed() {
        if (gameScreen==0) {
            startGame();
        }

        if (gameScreen==1) {
            for (Drop plate : plates) {
                plate.clicked();
                savedName = plate.getWord().replace("\n", " ");
                float ds = PApplet.dist(mouseX, mouseY, plate.getX(), plate.getY());
                if (ds < 16) {
                    plate.already_clicked = true;
                    checkTrue();
                }
            }
            for (Drop plate : plates) {
                if (plate.already_clicked) {
                    for (Drop all : plates) {
                        all.doubleySpeed();
                    }
                    break;
                }
            }
        }

        if (gameScreen==2) {
            restart();
        }
    }

    public void rain(int round) {
        ArrayList<FloatDict> plate_pos = new ArrayList<>();
        while (plate_pos.size() < no_of_plates) {
            FloatDict plate_x = new FloatDict();
            plate_x.set("x", random(32, width - 32));
            plate_x.set("y", random(-250, -100));
            plate_x.set("r", 64);

            boolean overlap = false;
            for (FloatDict others : plate_pos) {
                float d = dist(plate_x.get("x"), plate_x.get("y"), others.get("x"), others.get("y"));
                if (d < 64) {
                    overlap = true;
                    break;
                }
            }
            if (!overlap) {
                plate_pos.add(plate_x);
            }
        }
        if (round < ending_round) {
            for (int i = 0; i < plates.length; i++) {
                plates[i] = new Drop(this, plate_pos.get(i).get("x"), plate_pos.get(i).get("y"), plate_pos.get(i).get("r"));
            }
        }

        List<String> myArray = new ArrayList<>();
        myArray.add("Kevin Harris");
        myArray.add("Oliver Mann");
        myArray.add("Ben Scott");
        myArray.add("Isaac Mac");
        myArray.add("Robert Parrish");
        myArray.add("Mack Hammond");
        myArray.add("Zach Bear");
        myArray.add("Frankie Kole");
        myArray.add("Dustin Hale");
        myArray.add("Stanley Nova");

        for (Drop drop : plates) {
            Random rand = new Random();
            int randomIndex = rand.nextInt(myArray.size());
            String word = myArray.get(randomIndex);
            word = word.replace(" ", "\n");
            myArray.remove(randomIndex);
            drop.setWord(word);
        }
    }

    /********* SCREEN CONTENTS *********/

    public void initScreen() {
        background(236, 240, 241);
        textAlign(CENTER);
        fill(52, 73, 94);
        textSize(70);
        text("Music Tetris", width/2, height/2);
        textSize(15);
        text("Click To Start", width/2, height-30);
    }

    public void gameScreen() {
        background(0,255,255);
        for (Drop plate : plates) {
            plate.step();
            plate.render();
        }

        song[currentSong].play();

        String round = "Round = " + str(Round);
        fill(0);
        text(round, 700, 50);
        textAlign(CENTER, CENTER);

        String Score = "Score = " + str(score);
        fill(0);
        text(Score, 700, 70);
        textAlign(CENTER, CENTER);

        boolean flag = false;
        for (Drop plate : plates) {
            if (!plate.height_check()) {
                flag = true;
            }
        }

        if (!flag && Round <= ending_round) {
            rain(Round);
            incrementRound();
        }

        if (Round > ending_round) {
            gameOver();
        }
    }


    public void gameOverScreen() {
        song[0].pause();
        background(0,255,255);
        fill(0);
        textSize(35);
        text("Game Over", width/2, height/2);
        textAlign(CENTER, CENTER);
        textSize(20);
        text("Score = " + str(score), width/2, height * 3/4);
        textSize(15);
        text("Click To Restart", width/2, height-30);
    }

    public void startGame() {
        gameScreen=1;
    }
    public void gameOver() {
        gameScreen=2;
    }

    public void restart() {
        score = 0;
        Round = 0;
        gameScreen = 1;
    }

    public void increaseScore() {
        score += 5;
    }

    public void decreaseScore() {
        score -= 2;
    }

    public void checkTrue() {
        if (    savedName.equals("Kevin Harris")   ||
                savedName.equals("Oliver Mann")    ||
                savedName.equals("Ben Scott")      ||
                savedName.equals("Isaac Mac")      ||
                savedName.equals("Robert Parrish")) {
            increaseScore();
        }
        else {
            decreaseScore();
        }
    }

    public void incrementRound() {
        Round += 1;
        song[currentSong].pause();
        currentSong += 1;
    }
}