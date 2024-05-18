package com.st.eighteen_be.tournament.domain.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * packageName    : com.st.eighteen_be.tournament.domain.entity
 * fileName       : TournamentCategoryConverter
 * author         : ipeac
 * date           : 24. 5. 18.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 5. 18.        ipeac       최초 생성
 */
@Converter(autoApply = true)
public class TournamentCategoryConverter implements AttributeConverter<TournamentCategoryEnums, String> {

    @Override
    public String convertToDatabaseColumn(TournamentCategoryEnums attribute) {
        if (attribute == null) {
            return null;
        }

        return attribute.getCategory();
    }

    @Override
    public TournamentCategoryEnums convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return TournamentCategoryEnums.findByCategory(dbData);
    }
}