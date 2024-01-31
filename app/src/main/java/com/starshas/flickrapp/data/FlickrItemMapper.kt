package com.starshas.flickrapp.data

import com.starshas.flickrapp.data.models.FlickrItem
import com.starshas.flickrapp.data.models.FlickrDbItem
import com.starshas.flickrapp.data.models.Media
import com.starshas.flickrapp.data.models.MediaEntity

object FlickrItemMapper {
    fun mapModelListToEntityList(models: List<FlickrItem>): List<FlickrDbItem> {
        return models.map { model ->
            FlickrDbItem(
                title = model.title,
                link = model.link,
                media = MediaEntity(m = model.media.m),
                dateTaken = model.dateTaken,
                description = model.description,
                published = model.published,
                author = model.author,
                authorId = model.authorId,
                tags = model.tags
            )
        }
    }

    fun mapEntityListToModelList(entities: List<FlickrDbItem>): List<FlickrItem> {
        return entities.map { entity ->
            FlickrItem(
                title = entity.title,
                link = entity.link,
                media = Media(m = entity.media.m),
                dateTaken = entity.dateTaken,
                description = entity.description,
                published = entity.published,
                author = entity.author,
                authorId = entity.authorId,
                tags = entity.tags
            )
        }
    }
}
