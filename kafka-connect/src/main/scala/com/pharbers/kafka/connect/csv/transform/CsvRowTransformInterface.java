package com.pharbers.kafka.connect.csv.transform;

import java.util.List;
import java.util.Map;

/**
 * 功能描述
 *
 * @author dcs
 * @version 0.0
 * @tparam T 构造泛型参数
 * @note 一些值得注意的地方
 * @since 2019/07/02 15:45
 */
public interface CsvRowTransformInterface {
    String transform(List<String[]> rowWithTitles, Map<String, String> pop);
}
