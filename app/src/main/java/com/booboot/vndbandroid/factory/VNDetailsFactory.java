package com.booboot.vndbandroid.factory;

import com.booboot.vndbandroid.activity.VNDetailsActivity;
import com.booboot.vndbandroid.adapter.vndetails.VNDetailsElement;
import com.booboot.vndbandroid.bean.Anime;
import com.booboot.vndbandroid.bean.Category;
import com.booboot.vndbandroid.bean.Genre;
import com.booboot.vndbandroid.bean.Item;
import com.booboot.vndbandroid.bean.Language;
import com.booboot.vndbandroid.bean.Links;
import com.booboot.vndbandroid.bean.Platform;
import com.booboot.vndbandroid.bean.Relation;
import com.booboot.vndbandroid.bean.Screen;
import com.booboot.vndbandroid.bean.Tag;
import com.booboot.vndbandroid.bean.Vote;
import com.booboot.vndbandroid.util.SettingsManager;
import com.booboot.vndbandroid.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VNDetailsFactory {
    public final static String TITLE_INFORMATION = "Information";
    public final static String TITLE_DESCRIPTION = "Description";
    public final static String TITLE_GENRES = "Genres";
    public final static String TITLE_CHARACTERS = "Characters";
    public final static String TITLE_SCREENSHOTS = "Screenshots";
    public final static String TITLE_STATS = "Stats";
    public final static String TITLE_TAGS = "Tags";
    public final static String TITLE_RELEASES = "Releases";
    public final static String TITLE_RELATIONS = "Relations";
    public final static String TITLE_ANIME = "Anime";
    public final static String TITLE_PLATFORMS = "Platforms";
    public final static String TITLE_LANGUAGES = "Languages";

    public static LinkedHashMap<String, VNDetailsElement> getData(VNDetailsActivity activity) {
        LinkedHashMap<String, VNDetailsElement> expandableListDetail = new LinkedHashMap<>();
        Item vn = activity.getVn();

        List<String> infoLeft = new ArrayList<>();
        List<String> infoRight = new ArrayList<>();
        List<Integer> infoRightImages = new ArrayList<>();
        infoLeft.add("Title");
        infoRight.add(vn.getTitle());
        infoRightImages.add(-1);
        if (vn.getOriginal() != null) {
            infoLeft.add("Original title");
            infoRight.add(vn.getOriginal());
            infoRightImages.add(-1);
        }

        infoLeft.add("Released date");
        infoRight.add(Utils.getDate(vn.getReleased(), true));
        infoRightImages.add(-1);

        if (vn.getAliases() != null) {
            infoLeft.add("Aliases");
            infoRight.add(vn.getAliases().replace("\n", "<br>"));
            infoRightImages.add(-1);
        }

        infoLeft.add("Length");
        infoRight.add(vn.getLengthString());
        infoRightImages.add(vn.getLengthImage());

        infoLeft.add("Links");
        Links links = vn.getLinks();
        String htmlLinks = "";
        if (links.getWikipedia() != null) htmlLinks += "<a href=\"" + Links.WIKIPEDIA + links.getWikipedia() + "\">Wikipedia</a>";
        if (links.getEncubed() != null) htmlLinks += "<br><a href=\"" + Links.ENCUBED + links.getEncubed() + "\">Encubed</a>";
        if (links.getRenai() != null) htmlLinks += "<br><a href=\"" + Links.RENAI + links.getRenai() + "\">Renai</a>";
        infoRight.add(htmlLinks);
        infoRightImages.add(-1);

        List<String> description = new ArrayList<>();
        if (vn.getDescription() != null) {
            String descriptionWithoutSpoilers = vn.getDescription();
            if (!Tag.checkSpoilerLevel(activity, 2)) {
                descriptionWithoutSpoilers = descriptionWithoutSpoilers.replaceAll("\\[spoiler\\].*\\[/spoiler\\]", "");
            }
            description.add(descriptionWithoutSpoilers);
        }

        List<String> statLeft = new ArrayList<>();
        List<String> statRight = new ArrayList<>();
        List<Integer> statRightImages = new ArrayList<>();
        statLeft.add("Popularity");
        statRight.add(vn.getPopularity() + "%");
        statRightImages.add(vn.getPopularityImage());
        statLeft.add("Rating");
        statRight.add(vn.getRating() + " (" + Vote.getName(vn.getRating()) + ")<br>" + vn.getVotecount() + " votes total");
        statRightImages.add(vn.getRatingImage());

        setGenresSubmenu(activity);
        setCharactersSubmenu(activity);
        setReleasesSubmenu(activity);
        setTagsSubmenu(activity);
        setRelationsSubmenu(activity);
        setAnimesSubmenu(activity);
        setScreensSubmenu(activity);
        setPlatformsSubmenu(activity);
        setLanguagesSubmenu(activity);

        expandableListDetail.put(TITLE_INFORMATION, new VNDetailsElement(null, infoLeft, infoRight, infoRightImages, null, VNDetailsElement.TYPE_TEXT));
        expandableListDetail.put(TITLE_DESCRIPTION, new VNDetailsElement(null, description, null, null, null, VNDetailsElement.TYPE_TEXT));
        expandableListDetail.put(TITLE_GENRES, activity.getGenresSubmenu());
        expandableListDetail.put(TITLE_CHARACTERS, activity.getCharactersSubmenu());
        expandableListDetail.put(TITLE_SCREENSHOTS, activity.getScreensSubmenu());
        expandableListDetail.put(TITLE_STATS, new VNDetailsElement(null, statLeft, statRight, statRightImages, null, VNDetailsElement.TYPE_TEXT));
        expandableListDetail.put(TITLE_TAGS, activity.getTagsSubmenu());
        expandableListDetail.put(TITLE_RELEASES, activity.getReleasesSubmenu());
        expandableListDetail.put(TITLE_RELATIONS, activity.getRelationsSubmenu());
        expandableListDetail.put(TITLE_ANIME, activity.getAnimesSubmenu());
        expandableListDetail.put(TITLE_PLATFORMS, activity.getPlatformsSubmenu());
        expandableListDetail.put(TITLE_LANGUAGES, activity.getLanguagesSubmenu());

        return expandableListDetail;
    }

    public static void setLanguagesSubmenu(VNDetailsActivity activity) {
        List<String> languages = new ArrayList<>();
        List<Integer> languages_flags = new ArrayList<>();

        if (activity.getVn().getLanguages() == null) {
            activity.setLanguagesSubmenu(new VNDetailsElement(languages_flags, languages, null, null, null, VNDetailsElement.TYPE_TEXT));
        } else {
            for (String language : activity.getVn().getLanguages()) {
                languages.add(Language.FULL_TEXT.get(language));
                languages_flags.add(Language.FLAGS.get(language));
            }

            if (activity.getLanguagesSubmenu() == null) {
                activity.setLanguagesSubmenu(new VNDetailsElement(languages_flags, languages, null, null, null, VNDetailsElement.TYPE_TEXT));
            } else {
                activity.getLanguagesSubmenu().setPrimaryImages(languages_flags);
                activity.getLanguagesSubmenu().setPrimaryData(languages);
            }
        }
    }

    public static void setPlatformsSubmenu(VNDetailsActivity activity) {
        List<String> platforms = new ArrayList<>();
        List<Integer> platforms_images = new ArrayList<>();

        if (activity.getVn().getPlatforms() == null) {
            activity.setPlatformsSubmenu(new VNDetailsElement(platforms_images, platforms, null, null, null, VNDetailsElement.TYPE_TEXT));
        } else {
            for (String platform : activity.getVn().getPlatforms()) {
                platforms.add(Platform.FULL_TEXT.get(platform));
                platforms_images.add(Platform.IMAGES.get(platform));
            }

            if (activity.getPlatformsSubmenu() == null) {
                activity.setPlatformsSubmenu(new VNDetailsElement(platforms_images, platforms, null, null, null, VNDetailsElement.TYPE_TEXT));
            } else {
                activity.getPlatformsSubmenu().setPrimaryImages(platforms_images);
                activity.getPlatformsSubmenu().setPrimaryData(platforms);
            }
        }
    }

    public static void setAnimesSubmenu(VNDetailsActivity activity) {
        List<Integer> anime_ids = new ArrayList<>();
        List<String> anime_primary = new ArrayList<>();
        List<String> anime_secondary = new ArrayList<>();

        if (activity.getVn().getAnime() == null) {
            activity.setAnimesSubmenu(new VNDetailsElement(anime_ids, anime_primary, anime_secondary, null, null, VNDetailsElement.TYPE_SUBTITLE));
        } else {
            for (Anime anime : activity.getVn().getAnime()) {
                anime_ids.add(anime.getId());
                anime_primary.add(anime.getTitle_romaji());
                String title_kanji = anime.getTitle_kanji() != null ? anime.getTitle_kanji() + "\n" : "";
                String type = anime.getType() != null ? anime.getType() + " • " : "";
                String year = anime.getYear() > 0 ? anime.getYear() + "" : "";
                anime_secondary.add(title_kanji + type + year);
            }

            if (activity.getAnimesSubmenu() == null) {
                activity.setAnimesSubmenu(new VNDetailsElement(anime_ids, anime_primary, anime_secondary, null, null, VNDetailsElement.TYPE_SUBTITLE));
            } else {
                activity.getAnimesSubmenu().setPrimaryImages(anime_ids);
                activity.getAnimesSubmenu().setPrimaryData(anime_primary);
                activity.getAnimesSubmenu().setSecondaryData(anime_secondary);
            }
        }
    }

    public static void setRelationsSubmenu(VNDetailsActivity activity) {
        List<Integer> relation_ids = new ArrayList<>();
        List<String> relation_titles = new ArrayList<>();
        List<String> relation_types = new ArrayList<>();

        if (activity.getVn().getRelations() == null) {
            activity.setRelationsSubmenu(new VNDetailsElement(relation_ids, relation_titles, relation_types, null, null, VNDetailsElement.TYPE_SUBTITLE));
        } else {
            for (Relation relation : activity.getVn().getRelations()) {
                relation_titles.add(relation.getTitle());
                relation_types.add(Relation.TYPES.get(relation.getRelation()));
                relation_ids.add(relation.getId());
            }

            if (activity.getRelationsSubmenu() == null) {
                activity.setRelationsSubmenu(new VNDetailsElement(relation_ids, relation_titles, relation_types, null, null, VNDetailsElement.TYPE_SUBTITLE));
            } else {
                activity.getRelationsSubmenu().setPrimaryImages(relation_ids);
                activity.getRelationsSubmenu().setPrimaryData(relation_titles);
                activity.getRelationsSubmenu().setSecondaryData(relation_types);
            }
        }
    }

    public static void setTagsSubmenu(VNDetailsActivity activity) {
        List<String> tags = new ArrayList<>();
        List<Integer> tags_images = new ArrayList<>();
        List<Integer> tags_ids = new ArrayList<>();

        if (activity.getVn().getTags() == null) {
            activity.setTagsSubmenu(new VNDetailsElement(tags_images, tags, null, tags_ids, null, VNDetailsElement.TYPE_TEXT));
        } else {
            Map<String, Boolean> alreadyProcessedCategories = new HashMap<>();
            for (List<Number> catInfo : activity.getVn().getTags()) {
                if (!Tag.checkSpoilerLevel(activity, catInfo.get(2).intValue())) continue;
                Tag cat = Tag.getTags(activity).get(catInfo.get(0).intValue());
                if (cat != null && alreadyProcessedCategories.get(cat.getCat()) == null) {
                    alreadyProcessedCategories.put(cat.getCat(), true);
                    tags.add("<b>" + Category.CATEGORIES.get(cat.getCat()) + " :</b>");
                    tags_images.add(-1);
                    tags_ids.add(-1);
                    for (List<Number> tagInfo : activity.getVn().getTags()) {
                        if (!Tag.checkSpoilerLevel(activity, tagInfo.get(2).intValue())) continue;
                        Tag tag = Tag.getTags(activity).get(tagInfo.get(0).intValue());
                        if (tag != null && tag.getCat().equals(cat.getCat())) {
                            tags.add(tag.getName());
                            tags_images.add(Tag.getScoreImage(tagInfo));
                            tags_ids.add(tag.getId());
                        }
                    }
                }
            }

            if (activity.getTagsSubmenu() == null) {
                activity.setTagsSubmenu(new VNDetailsElement(tags_images, tags, null, tags_ids, null, VNDetailsElement.TYPE_TEXT));
            } else {
                activity.getTagsSubmenu().setPrimaryImages(tags_images);
                activity.getTagsSubmenu().setPrimaryData(tags);
                activity.getTagsSubmenu().setSecondaryImages(tags_ids);
            }
        }
    }

    public static void setGenresSubmenu(VNDetailsActivity activity) {
        List<String> genres = new ArrayList<>();

        if (activity.getVn().getTags() == null) {
            activity.setGenresSubmenu(new VNDetailsElement(null, genres, null, null, null, VNDetailsElement.TYPE_TEXT));
        } else {
            for (List<Number> tagInfo : activity.getVn().getTags()) {
                if (!Tag.checkSpoilerLevel(activity, tagInfo.get(2).intValue())) continue;
                Tag tag = Tag.getTags(activity).get(tagInfo.get(0).intValue());
                if (tag != null && Genre.contains(tag.getName())) {
                    genres.add(tag.getName());
                }
            }

            if (activity.getGenresSubmenu() == null) {
                activity.setGenresSubmenu(new VNDetailsElement(null, genres, null, null, null, VNDetailsElement.TYPE_TEXT));
            } else {
                activity.getGenresSubmenu().setPrimaryData(genres);
            }
        }
    }

    public static void setScreensSubmenu(VNDetailsActivity activity) {
        List<String> screenshots = new ArrayList<>();

        if (activity.getVn().getScreens() == null) {
            activity.setScreensSubmenu(new VNDetailsElement(null, screenshots, null, null, null, VNDetailsElement.TYPE_IMAGES));
        } else {
            for (Screen screenshot : activity.getVn().getScreens()) {
                if (screenshot.isNsfw() && !SettingsManager.getNSFW(activity)) continue;
                screenshots.add(screenshot.getImage());
            }

            if (activity.getScreensSubmenu() == null) {
                activity.setScreensSubmenu(new VNDetailsElement(null, screenshots, null, null, null, VNDetailsElement.TYPE_IMAGES));
            } else {
                activity.getScreensSubmenu().setPrimaryData(screenshots);
            }
        }
    }

    public static void setCharactersSubmenu(VNDetailsActivity activity) {
        List<String> images = new ArrayList<>();
        List<String> names = new ArrayList<>();
        List<String> subnames = new ArrayList<>();
        List<Integer> ids = new ArrayList<>();

        if (activity.getCharacters() == null) {
            activity.setCharactersSubmenu(new VNDetailsElement(ids, names, subnames, null, images, VNDetailsElement.TYPE_SUBTITLE));
        } else {
            for (Item character : activity.getCharacters()) {
                /* Checking the spoiler level of the whole character */
                boolean spoilerOk = true;
                /* Looping through the "vns" attribute because the spoiler lever is stored for each vn */
                for (Object[] spoilerInfo : character.getVns()) {
                    /* Checking only the character for the current VN */
                    if ((int) spoilerInfo[0] != activity.getVn().getId()) continue;
                    /* If at least one release tags the character's spoiler level beyond our desired spoiler level, we totally hide it */
                    if (!Tag.checkSpoilerLevel(activity, (int) spoilerInfo[2])) {
                        spoilerOk = false;
                        break;
                    }
                }
                if (!spoilerOk) continue;

                images.add(character.getImage());
                names.add(character.getName());
                subnames.add(Item.ROLES.get(character.getVns().get(0)[Item.ROLE_INDEX].toString()));
                ids.add(character.getId());
            }

            if (activity.getCharactersSubmenu() == null) {
                activity.setCharactersSubmenu(new VNDetailsElement(ids, names, subnames, null, images, VNDetailsElement.TYPE_SUBTITLE));
            } else {
                activity.getCharactersSubmenu().setPrimaryImages(ids);
                activity.getCharactersSubmenu().setPrimaryData(names);
                activity.getCharactersSubmenu().setSecondaryData(subnames);
                activity.getCharactersSubmenu().setUrlImages(images);
            }
        }
    }

    public static void setReleasesSubmenu(VNDetailsActivity activity) {
        List<Integer> images = new ArrayList<>();
        List<String> names = new ArrayList<>();
        List<String> subnames = new ArrayList<>();
        List<Integer> ids = new ArrayList<>();

        if (activity.getReleases() == null) {
            activity.setReleasesSubmenu(new VNDetailsElement(images, names, subnames, ids, null, VNDetailsElement.TYPE_SUBTITLE));
        } else {
            for (String language : activity.getReleases().keySet()) {
                images.add(Language.FLAGS.get(language));
                names.add("<b>" + Language.FULL_TEXT.get(language) + " :</b>");
                subnames.add(null);
                ids.add(null);
                for (Item release : activity.getReleases().get(language)) {
                    images.add(null);
                    names.add(release.getTitle());
                    subnames.add(Utils.getDate(release.getReleased(), true) + " • " + Utils.capitalize(release.getType()));
                    ids.add(release.getId());
                }

                if (activity.getReleasesSubmenu() == null) {
                    activity.setReleasesSubmenu(new VNDetailsElement(images, names, subnames, ids, null, VNDetailsElement.TYPE_SUBTITLE));
                } else {
                    activity.getReleasesSubmenu().setPrimaryImages(images);
                    activity.getReleasesSubmenu().setPrimaryData(names);
                    activity.getReleasesSubmenu().setSecondaryData(subnames);
                    activity.getReleasesSubmenu().setSecondaryImages(ids);
                }
            }
        }
    }
}