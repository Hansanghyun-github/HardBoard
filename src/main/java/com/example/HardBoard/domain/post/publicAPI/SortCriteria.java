package com.example.HardBoard.domain.post.publicAPI;

public enum SortCriteria {
    Accuracy, Recent, MostComments, MostRecommends, MostUnrecommends;

    public static SortCriteria lookup(String sortBase){
        try {
            return SortCriteria.valueOf(sortBase);
        } catch (IllegalArgumentException e){ throw new IllegalArgumentException("SortCriteria is wrong"); }
    }
}
