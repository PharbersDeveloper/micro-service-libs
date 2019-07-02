package com.pharbers.kafka.connect.csv.transform;

import com.sun.deploy.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 功能描述
 *
 * @author dcs
 * @version 0.0
 * @note 一些值得注意的地方
 * @since 2019/07/02 16:03
 */
public class CsvSelectColumnTransform implements CsvRowTransformInterface {
    private final String SELECT_TITLES_KEY = "select";

    @Override
    public String transform(List<String[]> rowWithTitle, Map<String, String> pop) {
        List<String> selectTitles = Arrays.asList(pop.get(SELECT_TITLES_KEY).split(","));
        List<String> row = rowWithTitle.stream().filter(x -> selectTitles.contains(x[0])).map(x -> x[1]).collect(Collectors.toList());
        return StringUtils.join(row, "");
    }
}
