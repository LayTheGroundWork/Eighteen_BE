package com.st.eighteen_be.common.converter;

import com.st.eighteen_be.tournament.domain.enums.GameRoundEnums;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * packageName    : com.st.eighteen_be.tournament.domain.entity
 * fileName       : GameRoundConverter
 * author         : ipeac
 * date           : 24. 5. 17.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 5. 17.        ipeac       최초 생성
 */
@Converter(autoApply = true)
public class GameRoundConverter implements AttributeConverter<GameRoundEnums, Integer> {
    @Override
    public Integer convertToDatabaseColumn(GameRoundEnums attribute) {
        if (attribute == null) {
            return null;
        }
        
        return attribute.getRound();
    }
    
    @Override
    public GameRoundEnums convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        
        return GameRoundEnums.getGameRound(dbData);
    }
}