package com.mycompany.data;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.model_objects.special.SnapshotResult;
import com.wrapper.spotify.model_objects.specification.AlbumSimplified;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Recommendations;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;
import com.wrapper.spotify.requests.data.browse.GetRecommendationsRequest;
import com.wrapper.spotify.requests.data.search.simplified.SearchAlbumsRequest;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SpotifyManager {

    private String client_id;
    private String client_secret;
    private SpotifyApi spotifyApi;
    GetRecommendationsRequest.Builder gmb;

    public SpotifyManager(String client_id, String client_secret) {
        this.client_id = client_id;
        this.client_secret = client_secret;
        this.authenticate();
        this.gmb = spotifyApi.getRecommendations()
                .limit(100)
                .max_popularity(50)
                .min_popularity(10)
                .target_popularity(20);
    }

    private void authenticate() {
        try {
            this.spotifyApi = new SpotifyApi.Builder().setClientId(this.client_id).setClientSecret(this.client_secret).build();
            ClientCredentials cc = this.spotifyApi.clientCredentials().build().execute();
            this.spotifyApi.setAccessToken(cc.getAccessToken());

        } catch (IOException | SpotifyWebApiException e) {
            e.printStackTrace();
        }
    }

    public SpotifyApi getSpotifyApi() {
        return spotifyApi;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getClient_secret() {
        return client_secret;
    }

    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }

    public void searchAlbum(String name) {
        SearchAlbumsRequest album = this.spotifyApi.searchAlbums("The Joshua Tree").build();

        try {
            Paging<AlbumSimplified> albumSimplifiedPaging = album.execute();
            for (AlbumSimplified i : albumSimplifiedPaging.getItems()) {
                System.out.println("album::" + i.getName());
            }

        } catch (SpotifyWebApiException | IOException e) {
            e.printStackTrace();
        }
    }
    
    public void createPlaylist(){
        
    }
    
    public void addTracksToPlaylist(String id, String[] uris){
        try {
            SnapshotResult currentPlaylist = spotifyApi.addTracksToPlaylist(id, uris).build().execute();
        } catch (IOException | SpotifyWebApiException ex) {
            Logger.getLogger(SpotifyManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void getRecommendation(String genre) {
        try {
            this.gmb.seed_genres(genre);
            Recommendations reco = this.gmb.build().execute();
            for (TrackSimplified t : reco.getTracks()) {
                for (ArtistSimplified a : t.getArtists()) {
                    System.out.println(a.getName() + ":" + a.getId() + "\t" + t.getName() + ":" + t.getId() +"\t"+ t.getUri());
                }
            }

        } catch (IOException | SpotifyWebApiException ex) {
            Logger.getLogger(SpotifyManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
