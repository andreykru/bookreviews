package pl.krutikov.bookreviews.dto.client;

import lombok.Data;

import java.util.List;

@Data
public class GoogleBooksResponse {

    private List<BookItem> items;

    @Data
    public static class BookItem {

        private String id;
        private VolumeInfo volumeInfo;

    }

    @Data
    public static class VolumeInfo {

        private String title;
        private List<String> authors;
        private String description;

    }

}
