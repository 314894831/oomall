package cn.edu.xmu.advertise.mapper;

import cn.edu.xmu.advertise.model.po.TimeSegmentPo;
import cn.edu.xmu.advertise.model.po.TimeSegmentPoExample;
import java.util.List;

public interface TimeSegmentPoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table time_segment
     *
     * @mbg.generated
     */
    int deleteByExample(TimeSegmentPoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table time_segment
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table time_segment
     *
     * @mbg.generated
     */
    int insert(TimeSegmentPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table time_segment
     *
     * @mbg.generated
     */
    int insertSelective(TimeSegmentPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table time_segment
     *
     * @mbg.generated
     */
    List<TimeSegmentPo> selectByExample(TimeSegmentPoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table time_segment
     *
     * @mbg.generated
     */
    TimeSegmentPo selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table time_segment
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(TimeSegmentPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table time_segment
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(TimeSegmentPo record);
}