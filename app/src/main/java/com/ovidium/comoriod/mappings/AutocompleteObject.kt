package com.ovidium.comoriod.mappings

import com.ovidium.comoriod.data.autocomplete.Hit

class AutocompleteObject(
    hit: Hit
) {

    val title: String = hit._source.title
    val author: String = hit._source.author
    val book: String = hit._source.book

}