package com.mycompany.processing;

import processing.core.PApplet;

public class Drop {

    private PApplet sketch;

    private float x;
    private float y;
    private float size;
    private float ySpeed;
    private String word;
    boolean already_clicked;

    public Drop(PApplet sketch, float x, float y, float size) {
        this.sketch = sketch;
        this.x = x;
        this.y = y;
        this.size = size;
        this.ySpeed = sketch.random(3, 6);
        this.already_clicked = false;
    }

    public void step() {
        y += ySpeed;
    }

    public void render() {
        if (already_clicked) {
            sketch.fill(50, 205, 50);
        } else {
            sketch.fill(255);
        }
        sketch.ellipse(x, y, size, size);
        sketch.fill(0);
        sketch.text(word, x, y);
        sketch.textAlign(sketch.CENTER, sketch.CENTER);
    }

    public void clicked() {
        float d = PApplet.dist(sketch.mouseX, sketch.mouseY, x, y);
        if (d < 16) {
            sketch.fill(50, 205, 50);
            sketch.ellipse(x, y, size, size);
            String world = word.replace("\n", " ");
            System.out.println(world);
        }
    }

    public boolean height_check() {
        return this.y > sketch.height;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void doubleySpeed() {
        ySpeed *= 2;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getWord() {
        return this.word;
    }
}
