/*
 * Copyright (c) 2014.
 * Cogz Development LLC USA
 * All Right reserved
 *
 * This software is the confidential and proprietary information of Cogz Development, LLC.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with Cogz LLC.
 */

package net.tbnr.gearz.activerecord;

import lombok.*;

import java.lang.reflect.Field;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString(includeFieldNames = true)
public class BasicAnalyzedField {
    @NonNull private final String key;
    private Object value;
    @NonNull private final Field field;
}
