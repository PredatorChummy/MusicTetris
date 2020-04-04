package com.mycompany.processing;

import processing.core.PApplet;
import processing.core.PFont;
import processing.data.FloatDict;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import processing.core.PVector;

import ddf.minim.*;
import ddf.minim.analysis.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class MySketch extends PApplet {

    HashMap<String, ArrayList<String>> genres_dict;
    HashMap<String, String> albums_dict;
    HashMap<String, String> artists_dict;
    HashMap<String, ArrayList<String>> album_tracks_dict;
    HashMap<String, ArrayList<String>> album_artists_dict;
    HashMap<String, ArrayList<String>> gold_res;
    
    List<String> myArray;

    int gameScreen = 0;
    int score = 0;
    int Round = 1;
    int currentSong = 0;
    int ending_round = 10;
    int no_of_plates = 5;
    String savedName;
    Drop[] plates = new Drop[no_of_plates];
    
    String [] languages = {"JAVA", "PHP", "JAVASCRIPT", "C/C++", "COBOL", 
  "VISUAL BASIC", "REBOL", "FORTRAN", "ADA"};

int N_LANGUAGES = languages.length;
    
    int BLUE_COLOR = color(52, 73, 94);

    
    int ball_size = 30;
    PVector [] posi;
    int current_choice = 0;
    
    

//    Minim minim;
    AudioPlayer[] song = new AudioPlayer[10];
    FFT fft;

    public void settings() {
        size(750, 750);
        smooth();
    }
    
    boolean rectOver = false;

    public void setup() {
        this.genres_dict = new HashMap<>();
        this.albums_dict = new HashMap<>();
        this.artists_dict = new HashMap<>();
        this.album_tracks_dict = new HashMap<>();
        this.album_artists_dict = new HashMap<>();
        this.gold_res = new HashMap<>();

        this.load_resources();
        
        this.myArray = new ArrayList<>();

        for (Map.Entry<String, ArrayList<String>> entry : genres_dict.entrySet()) {
            String key = entry.getKey();
            ArrayList<String> artists_ids = entry.getValue();
            
            for (String artist : artists_ids) {
                if (this.album_artists_dict.containsKey(artist)) {
                    for (String album : this.album_artists_dict.get(artist)) {
                        if (this.album_tracks_dict.containsKey(album)) {
                            for (String track : this.album_tracks_dict.get(album)) {
                                this.myArray.add(track);
                                if (!this.gold_res.containsKey(track)) {
                                    this.gold_res.put(track, new ArrayList<String>());
                                }
                                if (!this.gold_res.get(track).contains(key)) {
                                    this.gold_res.get(track).add(key);
//                                    System.out.println(this.gold_res.get(track));
                                }
                            }
                        }
                    }
                }
            }
        }
        

        PFont f = createFont("Arial", 16, true);
        textFont(f, 12);
        rain(Round);
//        minim = new Minim(this);

//        song[0] = minim.loadFile("01 Radioactive.mp3", 2048);
//        song[1] = minim.loadFile("01 Radioactive.mp3", 2048);
//        song[2] = minim.loadFile("01 Radioactive.mp3", 2048);
//        song[3] = minim.loadFile("01 Radioactive.mp3", 2048);
//        song[4] = minim.loadFile("01 Radioactive.mp3", 2048);
//        song[5] = minim.loadFile("01 Radioactive.mp3", 2048);
//        song[6] = minim.loadFile("01 Radioactive.mp3", 2048);
//        song[7] = minim.loadFile("01 Radioactive.mp3", 2048);
//        song[8] = minim.loadFile("01 Radioactive.mp3", 2048);
//        song[9] = minim.loadFile("01 Radioactive.mp3", 2048);

    posi = new PVector[N_LANGUAGES];

      for (int i = 0; i < N_LANGUAGES; i++) {
        posi[i] = new PVector(width/2, 175 + (i * 50) );
      }
    }

    public void draw() {
        if (gameScreen == 0) {
            initScreen();
        } else if (gameScreen == 1) {
            menuScreen();
        } else if (gameScreen == 2) {
            gameScreen();
        } else if (gameScreen == 3) {
            gameOverScreen();
        }
    }

    public void mousePressed() {
        if (gameScreen == 0) {
            startMenu();
        }
        
        if (gameScreen == 1) {
            
            if (rectOver) {
                gameScreen = 2;
            }
            
            for (int i = 0; i < N_LANGUAGES; i++) {
                if ( mouseX > (posi[i].x - 60) - (ball_size / 2) && mouseX < (posi[i].x - 60) + (ball_size / 2) &&
                  mouseY > posi[i].y - (ball_size / 2) && mouseY < posi[i].y + (ball_size / 2)  ) {
                  current_choice = i;
                }
            }
        }

        if (gameScreen == 2) {
            for (Drop plate : plates) {
                plate.clicked();
                savedName = plate.getWord().replace("\n", " ");
                float ds = PApplet.dist(mouseX, mouseY, plate.getX(), plate.getY());
                if (ds < 32) {
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

        if (gameScreen == 3) {
            restart();
        }
    }

    public void rain(int round) {
        int radius = 64;
        ArrayList<FloatDict> plate_pos = new ArrayList<>();
        while (plate_pos.size() < no_of_plates) {
            FloatDict plate_x = new FloatDict();
            plate_x.set("x", random(radius / 2, width - radius / 2));
            plate_x.set("y", random(-250, -100));
            plate_x.set("r", radius);

            boolean overlap = false;
            for (FloatDict others : plate_pos) {
                float d = dist(plate_x.get("x"), plate_x.get("y"), others.get("x"), others.get("y"));
                if (d < radius) {
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

        
        for (Drop drop : plates) {
            Random rand = new Random();
            int randomIndex = rand.nextInt(myArray.size());
            String word = myArray.get(randomIndex);
            word = word.replace(" ", "\n");
            myArray.remove(randomIndex);
            drop.setWord(word);
        }
    }

    /**
     * ******* SCREEN CONTENTS ********
     */
    public void initScreen() {
        background(236, 240, 241);
        textAlign(CENTER);
        fill(52, 73, 94);
        textSize(70);
        text("Music Tetris", width / 2, height / 2);
        textSize(15);
        text("Click To Start", width / 2, height - 30);
    }
    
    public void menuScreen() {
        background(236, 240, 241);;
        
        textAlign(CENTER);
        fill(BLUE_COLOR);
        textSize(50);
        text("Pick A Genre", width / 2, 100);
        
        textSize(15);
        for (int i = 0; i < N_LANGUAGES; i++) {
          fill(BLUE_COLOR);
          text(languages[i], posi[i].x + ball_size, posi[i].y + 5);

          noFill();
          stroke(BLUE_COLOR);
          strokeWeight(2);
          ellipse((posi[i].x - 60), posi[i].y, ball_size, ball_size);

          if (i == current_choice) {
                noStroke();
                fill(BLUE_COLOR);
                ellipse((posi[i].x - 60), posi[i].y, (ball_size - 7), (ball_size - 7));
            }
        }
        startButtonforGame();
    }
        
    public void startButtonforGame() {
          noStroke();
          rectMode(CENTER); 
          fill(BLUE_COLOR);
          int rect_width = 200;
          int rect_height = 50;
          int rect_pos_x = width/2;
          int rect_pos_y = height - 75;
          rect(rect_pos_x, rect_pos_y, rect_width, rect_height);
          
          textSize(15);
          fill(236, 240, 241);
          text("Click to Start Game", rect_pos_x, height - 70);
          
          if (overRect(rect_pos_x, rect_pos_y, rect_width, rect_height)) {
              rectOver = true;
          }
          else {
              rectOver = false;
          }
    }
    
    public boolean overRect(int x, int y, int width, int height) {
        if (mouseX >= x-width/2 && mouseX <= x+width/2 && mouseY >= y-height/2 && mouseY <= y+height/2) {
            return true;
          } else {
            return false;
        }
    }

    public void gameScreen() {
        background(0, 255, 255);
        for (Drop plate : plates) {
            plate.step();
            plate.render();
        }

//        song[currentSong].play();
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
//        song[0].pause();
        background(0, 255, 255);
        fill(0);
        textSize(35);
        text("Game Over", width / 2, height / 2);
        textAlign(CENTER, CENTER);
        textSize(20);
        text("Score = " + str(score), width / 2, height * 3 / 4);
        textSize(15);
        text("Click To Restart", width / 2, height - 30);
    }

    public void startMenu() {
        gameScreen = 1;
    }
    
    public void startGame() {
        gameScreen = 2;
    }

    public void gameOver() {
        gameScreen = 3;
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
        if (this.gold_res.get(savedName).contains("pop")) {
            increaseScore();
        } else {
            decreaseScore();
        }
    }

    public void incrementRound() {
        Round += 1;
//        song[currentSong].pause();
        currentSong += 1;
    }

    public void load_resources() {
        String line = null;
        try {
            FileInputStream fis = new FileInputStream("data" + File.separator + "albums" + File.separator + "album_index.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            while ((line = br.readLine()) != null) {
                String[] tokens = line.trim().split("\t");
                albums_dict.put(tokens[1], tokens[0]);
            }
            br.close();
            fis.close();

            fis = new FileInputStream("data" + File.separator + "albums" + File.separator + "artist_index.txt");
            br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            while ((line = br.readLine()) != null) {
                String[] tokens = line.trim().split("\t");
                artists_dict.put(tokens[1], tokens[0]);
                if (tokens.length > 2) {
                    for (String genre : tokens[2].trim().split("\\|")) {
                        if (!this.genres_dict.containsKey(genre)) {
                            this.genres_dict.put(genre, new ArrayList<>());
                        }
                        this.genres_dict.get(genre).add(tokens[1]);
                    }
                }
            }
            br.close();
            fis.close();

            fis = new FileInputStream("data" + File.separator + "albums" + File.separator + "album_track.txt");
            br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            while ((line = br.readLine()) != null) {
                String[] tokens = line.trim().split("\t");
                if (!album_tracks_dict.containsKey(tokens[0])) {
                    album_tracks_dict.put(tokens[0], new ArrayList<>());
                }
                album_tracks_dict.get(tokens[0]).add(tokens[2]);
            }
            br.close();
            fis.close();

            ArrayList<String> countries = new ArrayList<>();
            countries.add("AU");
            countries.add("GB");
            countries.add("IE");
            countries.add("US");

            for (String country : countries) {
                fis = new FileInputStream("data" + File.separator + "albums" + File.separator + country + "_album_artist.txt");
                br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
                while ((line = br.readLine()) != null) {
                    String[] tokens = line.trim().split("\t");
                    if (!album_artists_dict.containsKey(tokens[1])) {
                        album_artists_dict.put(tokens[1], new ArrayList<>());
                    }
                    album_artists_dict.get(tokens[1]).add(tokens[0]);
                }
                br.close();
                fis.close();

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
   

    public static void main(String[] args) {
        String[] processingArgs = {"MySketch"};
        MySketch mySketch = new MySketch();

        PApplet.runSketch(processingArgs, mySketch);
    }
}
