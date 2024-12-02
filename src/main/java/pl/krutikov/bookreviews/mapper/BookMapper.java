package pl.krutikov.bookreviews.mapper;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import pl.krutikov.bookreviews.domain.Book;
import pl.krutikov.bookreviews.dto.response.BookResponse;
import pl.krutikov.bookreviews.dto.client.GoogleBooksResponse;

@Mapper(componentModel = "spring")
public interface BookMapper {

    BookResponse toResponse(Book book);

    @Mapping(target = "id", source = "item.id")
    @Mapping(target = "title", source = "item.volumeInfo.title")
    @Mapping(target = "author", source = "item", qualifiedByName = "mapAuthors")
    @Mapping(target = "description", source = "item.volumeInfo.description")
    BookResponse toResponse(GoogleBooksResponse.BookItem item);

    @Mapping(target = "id", source = "item.id")
    @Mapping(target = "title", source = "item.volumeInfo.title")
    @Mapping(target = "author", source = "item", qualifiedByName = "mapAuthors")
    @Mapping(target = "description", source = "item.volumeInfo.description")
    Book toEntity(GoogleBooksResponse.BookItem item);

    @Named("mapAuthors")
    static String mapAuthors(GoogleBooksResponse.BookItem item) {
        return StringUtils.join(item.getVolumeInfo().getAuthors(), ", ");
    }

}
