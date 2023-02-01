package com.lemusee.lemusee_prj.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;

public class YoutubeService {

    @Value("${youtube.api.key}")
    private String YOUTUBE_API_KEY;
    private static final String YOUTUBE_API_BASE_URL = "https://www.googleapis.com/youtube/v3/playlists";

    public YouTubePlaylist fetchPlaylist(String playlistId) throws Exception {
        String url = YOUTUBE_API_BASE_URL + "?part=snippet&id=" + playlistId + "&key=" + YOUTUBE_API_KEY;

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder responseBuilder = new StringBuilder();
        String line;
        while ((line = responseReader.readLine()) != null) {
            responseBuilder.append(line);
        }
        responseReader.close();

        Gson gson = new Gson();
        YouTubePlaylist playlist = gson.fromJson(responseBuilder.toString(), YouTubePlaylist.class);

        return playlist;
    }
}
