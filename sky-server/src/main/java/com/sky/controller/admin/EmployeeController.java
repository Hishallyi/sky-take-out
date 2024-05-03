package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("员工登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        // 前端传递给后端的用DTO封装，后端接收DTO，然后调用service层的login方法进行登录
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        // 在此已经存入员工ID，所以可以对Token进行解析来获取员工ID
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        // 后端返回给前端的信息用VO封装，前端接收VO，然后展示给用户
        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation("员工退出")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 新增员工
     *
     * @param employeeDTO：员工信息
     * @return：Result
     */
    @PostMapping
    @ApiOperation("新增员工接口")
    public Result addEmployee(@RequestBody EmployeeDTO employeeDTO) {
        log.info("新增员工：{}", employeeDTO); // 花括号是占位符，运行时会替换成后面的employeeDTO对象
        employeeService.save(employeeDTO);
        return Result.success();
    }

    /**
     * 员工分页查询，由于前端传入的信息不是JSON格式，因此无需在形参中加入@RequestBody注解，直接在方法中接收参数即可
     * 其中@RequestParam注解常用语POST请求
     *
     * @param employeePageQueryDTO：分页查询条件
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("员工分页查询")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO) {
        log.info("员工分页查询：{}", employeePageQueryDTO);
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 设置员工状态
     *
     * @param status：状态，1表示启用，0表示停用
     * @param id：员工ID，通过地址栏传递
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用禁用员工账号")
    public Result startOrStop(@PathVariable Integer status, Long id) {
        log.info("设置员工状态：status={}, id={}", status, id);
        employeeService.startOrStop(status, id);
        return Result.success();
    }

    /**
     * 根据ID查询员工信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID查询员工信息")
    public Result<Employee> getById(@PathVariable Long id) {
        log.info("查询员工信息：id={}", id);
        return Result.success(employeeService.getById(id));
    }

    /**
     * 更新员工信息
     *
     * @param employeeDTO
     * @return
     */
    @PutMapping
    @ApiOperation("更新员工信息")
    public Result<String> update(@RequestBody EmployeeDTO employeeDTO){
        log.info("更新员工信息：{}", employeeDTO);
        employeeService.update(employeeDTO);
        return Result.success();
    }
}
