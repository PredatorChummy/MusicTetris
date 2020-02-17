package com.mycompany.data;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import com.wrapper.spotify.requests.data.albums.GetAlbumRequest;

public class SpotifyManager {

    public static void main(String[] args) {
        SpotifyApi spotifyApi = new SpotifyApi.Builder().setClientId("").setClientSecret("").build();
        GetAlbumRequest.Builder album = spotifyApi.getAlbum("The Joshua Tree");
        GetAlbumRequest build = album.build();
        System.out.println("album::"+album.build());
//                .("2570579e804b4ae8ac410c01f3e592d8")
//                .build();
//        spotifyApi.getSomething("qKRpDADUKrFeKhFHDMdfcu").market(CountryCode.SE).build();

    }
}
