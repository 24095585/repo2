package com.xiupeilian.carpart.mapper;

import com.xiupeilian.carpart.base.BaseMapper;
import com.xiupeilian.carpart.model.News;

import java.util.List;

public interface NewsMapper extends BaseMapper<News>{

    List<News> findNews();

}