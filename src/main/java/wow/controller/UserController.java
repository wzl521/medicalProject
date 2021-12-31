package wow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import wow.entity.BackJSON;
import wow.entity.UserInfo;
import wow.service.UserService;

import javax.servlet.http.HttpSession;


/**
 * 通用用户功能模块
 *
 * @author wow
 * @date 2020年6月6日
 */

@RestController
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private UserService us;

    /* 新用户注册 */
    @PostMapping("register")
//	public BackJSON register(@RequestBody UserInfo userInfo) {
    public BackJSON register(@RequestBody UserInfo userInfo) {
        return us.userRegister(userInfo);
    }

    /* 根据名称获取医疗机构ID */
    @GetMapping("getHospitalID")
    public BackJSON getHospitalID(String hospitalName) {
        return us.getHospitalID(hospitalName);
    }

    /* 用户登录 */
    @PostMapping("login")
    public BackJSON login(@RequestBody UserInfo userInfo, HttpSession httpSession) {
        System.out.println(userInfo.getUserPhone());
        httpSession.setAttribute("userInfo",userInfo);

        System.out.println(userInfo.getUserType());
        System.out.println(userInfo.getPassword());
        return us.userLogin(userInfo);
    }
    /* 查询公钥 */
//	@GetMapping("/getPublicKey")
	@RequestMapping(value = "/getPublicKey/{userID}",method = RequestMethod.GET)
	public BackJSON getPublicKey(@PathVariable("userID") Integer userID){
		return us.getPublicKey(userID);
	}
	/*生成新的公私钥对*/
    @RequestMapping(value = "/generateNewKey/{userID}",method = RequestMethod.POST)
	public BackJSON generateNewKey(@PathVariable("userID")Integer userID){
	    return us.generateNewKey(userID);
    }

}
