package com.springboot.project.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.function.Function;
import org.jinq.jpa.JPAJinqStream;
import org.jinq.orm.stream.JinqStream;
import org.jinq.tuples.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.springboot.project.common.database.JPQLFunction;
import com.springboot.project.properties.DatabaseJdbcProperties;
import cn.hutool.extra.spring.SpringUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class PaginationModel<T> {
    private Long pageNum;
    private Long pageSize;
    private Long totalRecord;
    private Long totalPage;
    private List<T> list;

    public PaginationModel(Long pageNum, Long pageSize, JinqStream<T> stream) {
        this(pageNum, pageSize, stream, s -> s);
    }

    public <U> PaginationModel(Long pageNum, Long pageSize, JinqStream<U> stream, Function<U, T> formatCallback) {
        if (pageNum < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page num must be greater than 1");
        }
        if (pageSize < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page size must be greater than 1");
        }
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        if (stream instanceof JPAJinqStream
                && !SpringUtil.getBean(DatabaseJdbcProperties.class).getIsSpannerEmulator()) {
            if (pageSize > 200) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Page size must be less than or equal to 200");
            }
            var list = stream.select(s -> new Pair<>(JPQLFunction.foundTotalRowsForGroupBy(), s))
                    .skip((pageNum - 1) * pageSize)
                    .limit(pageSize)
                    .toList();
            if (!list.isEmpty() || pageNum == 1) {
                this.totalRecord = JinqStream.from(list).select(Pair::getOne).findFirst().orElse(0L);
                this.setList(list.stream().map(Pair::getTwo).map(formatCallback).toList());
            } else {
                this.totalRecord = stream.select(s -> JPQLFunction.foundTotalRowsForGroupBy()).findFirst()
                        .orElse(0L);
                this.setList(list.stream().map(Pair::getTwo).map(formatCallback).toList());
            }
        } else {
            var dataList = stream.select(s -> s).toList();
            this.totalRecord = Long.valueOf(dataList.size());
            this.setList(JinqStream.from(dataList).skip((pageNum - 1) * pageSize).limit(pageSize).map(formatCallback)
                    .toList());
        }
        this.totalPage = new BigDecimal(this.totalRecord)
                .divide(new BigDecimal(pageSize), 0, RoundingMode.FLOOR)
                .longValue()
                + new BigDecimal(this.totalRecord).remainder(new BigDecimal(pageSize)).compareTo(BigDecimal.ZERO);
    }

}
