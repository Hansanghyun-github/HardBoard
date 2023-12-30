package com.example.HardBoard.domain.post.publicAPI;

public enum SearchCriteria {
    Title, Contents, TitleAndContents, Writer;

    public static SearchCriteria lookup(String searchCriteria){
        try {
            return SearchCriteria.valueOf(searchCriteria);
        } catch (IllegalArgumentException e){ throw new IllegalArgumentException("SearchCriteria is wrong"); }
    }
}
