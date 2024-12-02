package pl.krutikov.bookreviews.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.krutikov.bookreviews.domain.User;
import pl.krutikov.bookreviews.dto.request.RegisterUserRequest;
import pl.krutikov.bookreviews.dto.response.UserIdResponse;
import pl.krutikov.bookreviews.mapper.encoder.EncodedMapping;
import pl.krutikov.bookreviews.mapper.encoder.PasswordEncoderMapper;

@Mapper(
        componentModel = "spring",
        uses = PasswordEncoderMapper.class
)
public interface UserMapper {

    @Mapping(target = "password", source = "password", qualifiedBy = EncodedMapping.class)
    User toEntity(RegisterUserRequest userRequest);

    UserIdResponse toResponse(User user);

}