package com.jgxq.common.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * 
 * </p>
 *
 * @author smallsmart
 * @since 2020-12-10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerSpiderDto {
    private String name;
    private Integer number;

    public PlayerSpiderDto(String name, String number) {
        this.name = name;
        try {
            this.number = Integer.parseInt(number);
        } catch (NumberFormatException e) {
            this.number = 0;
        }
    }
}