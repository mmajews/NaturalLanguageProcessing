package tfidf

import java.util.stream.Collectors

class TermsHelper {
    static getTermsForString(String el) {
        el.split("\\s+").toList().stream().map {
            var -> var.toUpperCase().trim().replaceAll("\\p{P}", "")
        }.collect(Collectors.toList())
    }
}
