package com.mycompany.data;

import com.mycompany.runner.Runner;
import com.neovisionaries.i18n.CountryCode;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataManager {

    public static void write_categories(ArrayList<String> listCategories, CountryCode cc) {
        try {
            FileOutputStream fos = new FileOutputStream("data" + File.separator + "categories" + File.separator + cc.name() + "_categories.txt");
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, "utf-8"));

            for (String c : listCategories) {
                bw.write(c.trim() + "\n");
            }

            bw.close();
            fos.close();

//        sm.getRecommendation("rock");
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(Runner.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Runner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
