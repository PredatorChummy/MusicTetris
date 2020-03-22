package com.mycompany.runner;

import com.mycompany.data.DataManager;
import com.mycompany.data.SpotifyManager;
import java.util.ArrayList;
import com.neovisionaries.i18n.CountryCode;
import com.wrapper.spotify.model_objects.specification.AlbumSimplified;
import com.wrapper.spotify.model_objects.specification.Artist;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Runner {

    private ArrayList<CountryCode> countryCodes;
    private SpotifyManager sm;

    public Runner() {
        sm = new SpotifyManager("code", "code");
    }

    public void setCountries(CountryCode[] ccs) {
        countryCodes = new ArrayList<>();
        for (CountryCode cc : ccs) {
            countryCodes.add(cc);
        }
    }

    public void getCategoriesByCountry() {
        for (CountryCode cc : this.countryCodes) {
            System.out.println(cc.name());
            ArrayList<String> listCategories = this.sm.getListCategories(cc);
            DataManager.write_categories(listCategories, cc);
        }
    }

    public void getNewAlbums(CountryCode cc) {
        HashMap<String, String> albums_dict = new HashMap<>();
        try {
            String line = null;

            try {
                FileInputStream fis = new FileInputStream("data" + File.separator + "albums" + File.separator + "album_index.txt");
                BufferedReader br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
                while ((line = br.readLine()) != null) {
                    String[] tokens = line.trim().split("\t");
                    albums_dict.put(tokens[0], tokens[1]);
                }
                br.close();
                fis.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            FileOutputStream fos = new FileOutputStream("data" + File.separator + "albums" + File.separator + cc.name() + "_album_artist.txt", true);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));

            FileOutputStream fosar = new FileOutputStream("data" + File.separator + "albums" + File.separator + "artist_index.txt", true);
            BufferedWriter bwar = new BufferedWriter(new OutputStreamWriter(fosar, "UTF-8"));

            FileOutputStream fosal = new FileOutputStream("data" + File.separator + "albums" + File.separator + "album_index.txt", true);
            BufferedWriter bwal = new BufferedWriter(new OutputStreamWriter(fosal, "UTF-8"));

            Paging<AlbumSimplified> latestReleases = this.sm.getLatestReleases(cc);

            for (AlbumSimplified a : latestReleases.getItems()) {
                if (!albums_dict.containsKey(a.getName())) {
                    ArtistSimplified[] artists = a.getArtists();

                    StringBuilder sb = new StringBuilder();
                    for (ArtistSimplified ar : artists) {
                        sb.append("|").append(ar.getId());

                        bwar.write(ar.getName() + "\t" + ar.getId());
                        bwar.newLine();
                    }

                    bw.write(a.getId() + "\t" + sb.substring(1));
                    bw.newLine();

                    bwal.write(a.getName() + "\t" + a.getId());
                    bwal.newLine();
                }

//                for (TrackSimplified t : albumTracks.getItems()) {
//                    System.out.println("Track:" + t.getName());
//                }
//                break;
            }
            bw.close();
            fos.close();

            bwar.close();
            fosar.close();

            bwal.close();
            fosal.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void populateAlbumTracks() {
        Set<String> albums_parsed = new HashSet<>();

        try {
            String line;

            try {
                FileInputStream fis = new FileInputStream("data" + File.separator + "albums" + File.separator + "album_track.txt");
                BufferedReader br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
                while ((line = br.readLine()) != null) {
                    String[] tokens = line.trim().split("\t");
                    albums_parsed.add(tokens[0]);
                }
                br.close();
                fis.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            FileOutputStream fos = new FileOutputStream("data" + File.separator + "albums" + File.separator + "album_track.txt", true);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));

            FileInputStream fis = new FileInputStream("data" + File.separator + "albums" + File.separator + "album_index.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));

            while ((line = br.readLine()) != null) {
                String[] tokens = line.trim().split("\t");
                if (!albums_parsed.contains(tokens[1])) {
                    Paging<TrackSimplified> albumTracks = this.sm.getAlbumTracks(tokens[1]);
                    for (TrackSimplified t : albumTracks.getItems()) {
                        bw.write(tokens[1] + "\t" + t.getId() + "\t" + t.getName());
                        bw.newLine();
                    }
                }

            }
            br.close();
            fis.close();

            bw.close();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void populateArtistDetails() {
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            FileInputStream fis = new FileInputStream("data" + File.separator + "albums" + File.separator + "artist_index.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));

            while ((line = br.readLine()) != null) {
                String[] tokens = line.trim().split("\t");
                if (tokens.length < 3 || tokens[2].equals("no_genre")) {
                    Artist ad = this.sm.getArtistDetails(tokens[1]);

                    String[] genres = ad.getGenres();
                    if (genres.length > 0) {
                        StringBuilder sbg = new StringBuilder();

                        for (String genre : genres) {
                            sbg.append("|").append(genre);
                        }
                        sb.append(line.trim() + "\t" + sbg.substring(1)).append("\n");
                    } else {
                        sb.append(line.trim() + "\t" + "no_genre").append("\n");
                    }
                } else {
                    sb.append(line.trim()).append("\n");
                }

            }
            br.close();
            fis.close();

            FileOutputStream fos = new FileOutputStream("data" + File.separator + "albums" + File.separator + "artist_index_update.txt");
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));

            bw.write(sb.toString());

            bw.close();
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        Runner r = new Runner();
        r.setCountries(new CountryCode[]{CountryCode.AU, CountryCode.GB, CountryCode.IE, CountryCode.US});

//        r.getCategoriesByCountry();
//        r.getNewAlbums(CountryCode.IE);
//        r.getNewAlbums(CountryCode.AU);
//        r.getNewAlbums(CountryCode.GB);
//        r.getNewAlbums(CountryCode.US);
//        r.populateAlbumTracks();
        r.populateArtistDetails();
    }
}
