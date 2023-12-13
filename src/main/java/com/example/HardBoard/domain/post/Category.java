package com.example.HardBoard.domain.post;

public enum Category {
    All, Fun, Sport, Politics, Game, Chat;

    public static Category lookup(String category){
        try {
            return Category.valueOf(category);
        } catch (IllegalArgumentException e){ throw new IllegalArgumentException("Category is wrong"); }
    }
}
