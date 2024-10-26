package com.st.eighteen_be.common.converter;

import com.st.eighteen_be.user.enums.CategoryType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * packageName    : com.st.eighteen_be.common.converter
 * fileName       : CategoryTypeConverter
 * author         : ipeac
 * date           : 24. 10. 26.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 10. 26.        ipeac       최초 생성
 */
@Converter(autoApply = true)
public class CategoryTypeConverter implements AttributeConverter<CategoryType, String> {
    @Override
    public String convertToDatabaseColumn(CategoryType categoryType) {
        return categoryType.getCategory();
    }
    
    @Override
    public CategoryType convertToEntityAttribute(String s) {
        return CategoryType.of(s);
    }
}
