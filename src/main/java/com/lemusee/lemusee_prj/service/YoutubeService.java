package com.lemusee.lemusee_prj.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.lemusee.lemusee_prj.domain.Community;
import com.lemusee.lemusee_prj.domain.Member;
import com.lemusee.lemusee_prj.dto.YoutubeDto;
import com.lemusee.lemusee_prj.repository.CommunityRepository;
import com.lemusee.lemusee_prj.repository.MemberRepository;
import com.lemusee.lemusee_prj.util.type.Role;
import com.lemusee.lemusee_prj.util.type.Team;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class YoutubeService {
    @Value("${youtube.api.key}")
    private String YOUTUBE_API_KEY;
    private static final String YOUTUBE_API_BASE_URL = "https://www.googleapis.com/youtube/v3/playlists";
    private final CommunityRepository communityRepository;

    public void addPlaylistVideos(String playlistId) throws IOException, ParseException {
        String url = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=50&playlistId=" + playlistId + "&key=" + YOUTUBE_API_KEY;

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        JSONObject json = new JSONObject(response.toString());
        JSONArray items = json.getJSONArray("items");

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        List<YoutubeDto> youtubeDtos = new ArrayList<>();
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            JSONObject snippet = item.getJSONObject("snippet");
            JSONObject thumbnails = snippet.getJSONObject("thumbnails");
            JSONObject mediumThumbnail = thumbnails.getJSONObject("medium");

            String videoId = item.getString("id");
            String title = snippet.getString("title");
            String description = snippet.getString("description");
            String thumbnailUrl = mediumThumbnail.getString("url");
            String publishedAt = snippet.getString("publishedAt");
            Date publishedAtDate = format.parse(publishedAt);

            YoutubeDto youtubeDto = YoutubeDto.builder()
                    .playlistId(playlistId)
                    .videoId(videoId)
                    .title(title)
                    .description(description)
                    .thumbnailUrl(thumbnailUrl)
                    .publishedAt(publishedAtDate)
                    .build();
            youtubeDtos.add(youtubeDto);
        }
        List<Community> communities = youtubeDtos.stream()
                .map(YoutubeDto:: toCommunity)
                .collect(Collectors.toList());

        communityRepository.saveAll(communities);
    }
}
