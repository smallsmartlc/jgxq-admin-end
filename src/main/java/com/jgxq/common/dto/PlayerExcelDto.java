package com.jgxq.common.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.fastjson.JSON;
import com.jgxq.admin.entity.Player;
import com.jgxq.common.define.Position;
import com.jgxq.common.define.StrongFoot;
import lombok.Data;
import lombok.ToString;
import org.springframework.beans.BeanUtils;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 
 * </p>
 *
 * @author smallsmart
 * @since 2021-12-28
 */
@Data
@ToString
public class PlayerExcelDto {

    @ExcelProperty("姓名")
    private String name;

    @ExcelProperty("英文名")
    private String enName;

    @ExcelProperty("身高")
    private Integer height;

    @ExcelProperty("体重")
    private Integer weight;

    @ExcelProperty("国籍")
    private String nation;

    @ExcelProperty("号码")
    private Integer number;

    @ExcelProperty("惯用脚")
    private String strongFoot;

    @ExcelProperty("位置")
    private String position;

    @ExcelProperty("生日")
    private Date birthday;

    /**
     * 基本信息
     */
    private String infos;

    public Player getEntity(){
        Player player = new Player();
        BeanUtils.copyProperties(this,player);
        Map<String, Integer> positionMap = Stream.of(Position.values())
                .collect(Collectors.toMap(Position::getPosition, Position::getValue));
        Map<String, Integer> footMap = Stream.of(StrongFoot.values())
                .collect(Collectors.toMap(StrongFoot::getFoot, StrongFoot::getValue));
        player.setPosition(positionMap.getOrDefault(this.position,0).byteValue());
        player.setStrongFoot(footMap.getOrDefault(this.strongFoot,0).byteValue());
        player.setInfos(JSON.toJSONString(new PlayerInfos(Collections.EMPTY_LIST)));
        return player;
    }

}
