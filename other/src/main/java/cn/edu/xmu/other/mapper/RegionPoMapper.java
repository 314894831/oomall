package cn.edu.xmu.other.mapper;

import cn.edu.xmu.other.model.po.RegionPo;
import cn.edu.xmu.other.model.po.RegionPoExample;
import java.util.List;

public interface RegionPoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table region
     *
     * @mbg.generated
     */
    int deleteByExample(RegionPoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table region
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table region
     *
     * @mbg.generated
     */
    int insert(RegionPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table region
     *
     * @mbg.generated
     */
    int insertSelective(RegionPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table region
     *
     * @mbg.generated
     */
    List<RegionPo> selectByExample(RegionPoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table region
     *
     * @mbg.generated
     */
    RegionPo selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table region
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(RegionPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table region
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(RegionPo record);
}