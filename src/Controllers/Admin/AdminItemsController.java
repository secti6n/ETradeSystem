package Controllers.Admin;

import Helpers.ModelHelper;
import Helpers.MsgHelper;
import Helpers.PageHelper;
import Models.ItemsEntity;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;

@Controller
public class AdminItemsController {
    //默认页数
    static int PageSize = 10;

    /**
     * @RequestParam注解的作用是：根据参数名从URL中取得参数值
     * @param page
     *             页数
     * @param model
     *            一个域对象，可用于存储数据值
     */
    @RequestMapping(value = "/admin/items", method =  RequestMethod.GET)
    public String index(@RequestParam(value = "page", defaultValue = "1") String page, Model model) {
        try{
            //参数预处理
            Integer currentPage = Integer.valueOf(page);

            List list = PageHelper.pagenation(ItemsEntity.class, currentPage);

            model.addAttribute("List", list);

            //获取总记录数，计算总页面数，传递总页面数
            int total_page = PageHelper.pageCount(ItemsEntity.class);
            model.addAttribute("totalPage", total_page);

            return "admin/items/index";
        }catch(Exception e) {
            //添加错误信息
            e.printStackTrace();
            model.addAttribute("error_msg", " 系统发生了错误！");
            return "admin/fail";
        }
    }



    /**
     * @RequestParam注解的作用是：根据参数名从URL中取得参数值
     * @param id
     *            商品 id
     * @param page
     *            当前页数
     * @param model
     *            一个域对象，可用于存储数据值
     * @param session
     *            线程对象
     */
    @RequestMapping(value = "/admin/items/delete", method =  RequestMethod.POST)
    public String delete(@RequestParam(value = "id") String id,
                         @RequestParam(value = "page", defaultValue = "1") String page,
                         Model model,
                         HttpSession session) {
        try{
            //参数预处理
            Integer currentPage = Integer.valueOf(page);
            Integer itemId = Integer.valueOf(id);

            //进行删除操作
            ItemsEntity item = (ItemsEntity) ModelHelper.getSingle(ItemsEntity.class,Restrictions.eq("id", itemId));

            if(item.getStatus() == ItemsEntity.STATUS_OFF) {
                //返回信息
                MsgHelper.setMsg(session, "商品ID:" + id + "上架成功！");
                item.setStatus(ItemsEntity.STATUS_ON);
            } else {
                MsgHelper.setMsg(session, "商品ID:" + id + "下架成功！");
                item.setStatus(ItemsEntity.STATUS_OFF);
            }

            ModelHelper.updateSingle(item);

            return "redirect:/admin/items?page=" + currentPage;
        }catch(Exception e) {
            //添加错误信息
            e.printStackTrace();
            model.addAttribute("error_msg", " 系统发生了错误！");
            return "admin/fail";
        }
    }

    /**
     * @RequestParam注解的作用是：根据参数名从URL中取得参数值
     * @param id
     *            商品 id
     * @param model
     *            一个域对象，可用于存储数据值
     */
    @RequestMapping(value = "/admin/items/edit", method = RequestMethod.GET)
    public String edit_show(@RequestParam(value = "id") String id,
                            Model model) {
        try{
            //参数预处理
            Integer itemId = Integer.valueOf(id);

            ItemsEntity item = (ItemsEntity) ModelHelper.getSingle(ItemsEntity.class,Restrictions.eq("id", itemId));

            //给管理页面加上要编辑的信息
            model.addAttribute("Item", item);

            return "admin/items/edit";
        }catch(Exception e) {
            //添加错误信息
            e.printStackTrace();
            model.addAttribute("error_msg", " 系统发生了错误！");
            return "admin/fail";
        }
    }


    /**
     * @RequestParam注解的作用是：根据参数名从URL中取得参数值
     * @param page
     *            当前页数
     * @param id
     *            当前商品 ID
     * @param item_name
     *            商品名称
     * @param item_price
     *            商品价格
     * @param item_img_uri
     *            商品头图链接
     * @param item_sku
     *            商品库存
     * @param item_description
     *            商品描述
     * @param model
     *            一个域对象，可用于存储数据值
     * @param session
     *            线程对象
     */
    @RequestMapping(value = "/admin/items/edit", method = RequestMethod.POST)
    public String edit(@RequestParam(value = "page", defaultValue = "1") String page,
                       @RequestParam(value = "id") String id,
                       @RequestParam(value = "item_name") String item_name,
                       @RequestParam(value = "item_price") String item_price,
                       @RequestParam(value = "item_img_uri", required = false) String item_img_uri,
                       @RequestParam(value = "item_sku") String item_sku,
                       @RequestParam(value = "item_description") String item_description,
                       Model model,
                       HttpSession session) {
        try{
            //参数预处理
            Integer currentPage = Integer.valueOf(page);
            BigDecimal item_price_d = new BigDecimal(item_price);
            Integer item_sku_i = new Integer(item_sku);
            Integer itemId = Integer.valueOf(id);

            if(item_description.equals("")) {
                item_description = "<p>卖家很懒，并没有写描述。</p>";
            }

            //输入参数为空
            if(item_name.equals("")) {
                //返回 错误信息
                MsgHelper.setMsg(session, "添加失败，参数不足！");
                return "redirect:/admin/items?page=" + currentPage;
            }

            ItemsEntity item = (ItemsEntity) ModelHelper.getSingle(ItemsEntity.class,Restrictions.eq("id", itemId));

            item.setItemName(item_name);
            item.setItemPrice(item_price_d);

            if(!item_img_uri.equals("")) {
                item.setItemImgUri(item_img_uri);
            }

            item.setItemSku(item_sku_i);
            item.setItemDescription(item_description);

            ModelHelper.updateSingle(item);

            //返回信息
            MsgHelper.setMsg(session, "ID:" + id + "编辑成功！");
            return "redirect:/admin/items?page=" + currentPage;
        }catch(Exception e) {
            //添加错误信息
            e.printStackTrace();
            model.addAttribute("error_msg", "系统发生了错误！");
            return "admin/fail";
        }
    }

    /**
     * @RequestParam注解的作用是：根据参数名从URL中取得参数值
     * @param id
     *            商品 id
     * @param model
     *            一个域对象，可用于存储数据值
     */
    @RequestMapping(value = "/admin/items/detail", method = RequestMethod.GET)
    public String detail(@RequestParam(value = "id") String id,
                            Model model) {
        try{
            //参数预处理
            Integer itemId = Integer.valueOf(id);

            ItemsEntity item = (ItemsEntity) ModelHelper.getSingle(ItemsEntity.class,Restrictions.eq("id", itemId));

            //给管理页面加上要编辑的信息
            model.addAttribute("Item", item);

            return "admin/items/detail";
        }catch(Exception e) {
            //添加错误信息
            e.printStackTrace();
            model.addAttribute("error_msg", " 系统发生了错误！");
            return "admin/fail";
        }
    }
}
