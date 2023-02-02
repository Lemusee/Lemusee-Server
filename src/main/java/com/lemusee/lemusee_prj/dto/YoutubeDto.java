package com.lemusee.lemusee_prj.dto;

import com.lemusee.lemusee_prj.domain.Community;
import com.lemusee.lemusee_prj.domain.Member;
import com.lemusee.lemusee_prj.util.type.Role;
import com.lemusee.lemusee_prj.util.type.Team;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class YoutubeDto {

    private String playlistId;

    private String videoId;

    private String title;

    private String description;

    private String thumbnailUrl;

    private Date publishedAt;

    public Community toCommunity() {
        return Community.builder()
                .playListId(playlistId)
                .videoId(videoId)
                .title(title)
                .description(description)
                .thumbnailUrl(thumbnailUrl)
                .publishedAt(publishedAt)
                .build();
    }
}
