package com.mycompany.data;

import com.wrapper.spotify.SpotifyApi;

public class SpotifyManager {

    private String client_id;
    private String client_secret;
    private final SpotifyApi spotifyApi;

    public SpotifyManager(String client_id, String client_secret) {
        this.client_id = client_id;
        this.client_secret = client_secret;
        this.spotifyApi = new SpotifyApi.Builder().setClientId(this.client_id).setClientSecret(this.client_secret).build();

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
}
