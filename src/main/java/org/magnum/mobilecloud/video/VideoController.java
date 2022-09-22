/*
 *
 * Copyright 2014 Jules White
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.magnum.mobilecloud.video;

import org.magnum.mobilecloud.video.client.VideoSvcApi;
import org.magnum.mobilecloud.video.repository.Video;
import org.magnum.mobilecloud.video.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import retrofit.http.POST;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.magnum.mobilecloud.video.client.VideoSvcApi.*;

@RestController
public class VideoController {

    /**
     * You will need to create one or more Spring controllers to fulfill the
     * requirements of the assignment. If you use this file, please rename it
     * to something other than "AnEmptyController"
     * <p>
     * <p>
     * ________  ________  ________  ________          ___       ___  ___  ________  ___  __
     * |\   ____\|\   __  \|\   __  \|\   ___ \        |\  \     |\  \|\  \|\   ____\|\  \|\  \
     * \ \  \___|\ \  \|\  \ \  \|\  \ \  \_|\ \       \ \  \    \ \  \\\  \ \  \___|\ \  \/  /|_
     * \ \  \  __\ \  \\\  \ \  \\\  \ \  \ \\ \       \ \  \    \ \  \\\  \ \  \    \ \   ___  \
     * \ \  \|\  \ \  \\\  \ \  \\\  \ \  \_\\ \       \ \  \____\ \  \\\  \ \  \____\ \  \\ \  \
     * \ \_______\ \_______\ \_______\ \_______\       \ \_______\ \_______\ \_______\ \__\\ \__\
     * \|_______|\|_______|\|_______|\|_______|        \|_______|\|_______|\|_______|\|__| \|__|
     */
    @Autowired
    VideoRepository videoRepository;

    @RequestMapping(value = "/go", method = RequestMethod.GET)
    public @ResponseBody String goodLuck() {
        return "Good Luck!";
    }

    /**
     * @return
     */
    @GetMapping(VIDEO_SVC_PATH)
    public Collection<Video> getVideoList() {
        List<Video> videos = new ArrayList<>();
        videoRepository.findAll().forEach(videos::add);
        return videos;
    }

    /**
     * @param id
     * @return
     */
    @GetMapping(VIDEO_SVC_PATH + "/{id}")
    public Video getVideoById(@PathVariable("id") long id, HttpServletResponse response) {
        Optional<Video> optionalVideo = videoRepository.findById(id);
        if (optionalVideo.isPresent()) {
            Video video = optionalVideo.get();
            return video;
        } else {
            response.setStatus(404);
        }
        return null;
    }

    /**
     * @param v
     * @return
     */
    @PostMapping(VIDEO_SVC_PATH)
    public Video addVideo(@RequestBody Video v) {
        videoRepository.save(v);
        return v;
    }

    /**
     * @param id
     * @return
     */
    @PostMapping(VIDEO_SVC_PATH + "/{id}/like")
    public Void likeVideo(@AuthenticationPrincipal User user, @PathVariable long id, HttpServletResponse response) {
        Optional<Video> optionalVideo = videoRepository.findById(id);
        if (optionalVideo.isPresent()) {
            Video video = optionalVideo.get();
            var likedBySet = video.getLikedBy();
            if (!likedBySet.contains(user.getUsername())){
                long currentLike = video.getLikes();
                likedBySet.add(user.getUsername());
                video.setLikes(currentLike + 1);
                video.setLikedBy(likedBySet);
                videoRepository.save(video);
            }else{
                response.setStatus(400);
            }
            return null;
        } else {
            response.setStatus(404);
        }
        return null;
    }

    /**
     * @param id
     * @return
     */
    @PostMapping(VIDEO_SVC_PATH + "/{id}/unlike")
    public Void unlikeVideo(@AuthenticationPrincipal User user, @PathVariable long id, HttpServletResponse response) {
        Optional<Video> optionalVideo = videoRepository.findById(id);
        if (optionalVideo.isPresent()) {
            Video video = optionalVideo.get();
            var likedBySet = video.getLikedBy();
            if (likedBySet.contains(user.getUsername())) {
                long currentLike = video.getLikes();
                likedBySet.remove(user.getUsername());
                video.setLikes(currentLike - 1);
                video.setLikedBy(likedBySet);
                videoRepository.save(video);
            }
            return null;
        } else {
            response.setStatus(404);
        }
        return null;
    }

    /**
     * @param title
     * @return
     */
    @GetMapping(VIDEO_TITLE_SEARCH_PATH)
    public Collection<Video> findByTitle(@RequestParam(TITLE_PARAMETER) String title) {
        return videoRepository.findAllByName(title);
    }

    /**
     * @param duration
     * @return
     */
    @GetMapping(VIDEO_DURATION_SEARCH_PATH)
    public Collection<Video> findByDurationLessThan(@RequestParam(DURATION_PARAMETER) long duration) {
        return videoRepository.findAllByDurationLessThan(duration);
    }
}
