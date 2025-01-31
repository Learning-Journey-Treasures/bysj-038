package com.daowen.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.daowen.service.SpcommentService;
import com.daowen.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.daowen.entity.Comment;
import com.daowen.service.CommentService;
import com.daowen.ssm.simplecrud.SimpleController;
import com.daowen.webcontrol.PagerMetal;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CommentController extends SimpleController {

	@Autowired
	private CommentService commentSrv=null;
	@Autowired
	private SpcommentService spcommentSrv=null;

	@Override
	@RequestMapping("/admin/commentmanager.do")
	public void mapping(HttpServletRequest request, HttpServletResponse response) {
		mappingMethod(request,response);
	}

	@ResponseBody
	@PostMapping("/admin/spcomment/list")
	public JsonResult  spcomment(){
		String id=request.getParameter("id");
		if(id==null)
			return JsonResult.error(-1,"参数异常");
		String sql=" select c.* ,h.accountname ,h.name ,h.touxiang from spcomment c ,huiyuan h where h.id=c.appraiserid and c.spid="+id;
		List<HashMap<String,Object>> listMap=spcommentSrv.queryToMap(sql);
		return JsonResult.success(1,"获取评论信息",listMap);
	}

	public void delete() {
		String[] ids = request.getParameterValues("ids");
		if (ids == null)
			return;
		String spliter = ",";
		String SQL = " where id in(" + join(spliter, ids) + ")";
		System.out.println("sql=" + SQL);
		commentSrv.delete(SQL);
	}

	public void save() {
		String photo = request.getParameter("photo");

		String commentren = request.getParameter("currenthy");
		String commentcontent = request.getParameter("commentcontent");
		String xtype = request.getParameter("xtype");
		String belongid = request.getParameter("belongid");
		String istopic=request.getParameter("istopic");
		String topicid=request.getParameter("topicid");
		Comment comment = new Comment();
		comment.setPhoto(photo);
		comment.setCommenttime(new Date());

		comment.setCommentren(commentren == null ? "" : commentren);
		comment.setCommentcontent(commentcontent == null ? "" : commentcontent);
		comment.setXtype(xtype == null ? "" : xtype);
		comment.setBelongid(belongid == null ? "" : belongid);

		if(istopic!=null)
			comment.setTopicid(new Integer(topicid));
		else
			comment.setTopicid(0);
		if(topicid!=null)
			comment.setIstopic(new Integer(istopic));
		else
			comment.setTopicid(0);
		commentSrv.save(comment);

		String forwardurl = request.getParameter("forwardurl");
		redirect(forwardurl);

	}


	public void get() {
		String filter = "";
		//
		String commentren = request.getParameter("commentren");
		if (commentren != null)
			filter = "  where commentren like '%" + commentren + "%'  ";
		int pageindex = 1;
		int pagesize = 10;
		// 获取当前分页
		String currentpageindex = request.getParameter("currentpageindex");
		// 当前页面尺寸
		String currentpagesize = request.getParameter("pagesize");
		// 设置当前页
		if (currentpageindex != null)
			pageindex = new Integer(currentpageindex);
		// 设置当前页尺寸
		if (currentpagesize != null)
			pagesize = new Integer(currentpagesize);
		List<Comment> listcomment = commentSrv.getPageEntitys(filter,pageindex, pagesize);
		int recordscount =  commentSrv.getRecordCount(filter == null ? "" : filter);
		request.setAttribute("listcomment", listcomment);
		PagerMetal pm = new PagerMetal(recordscount);
		// 设置尺寸
		pm.setPagesize(pagesize);
		// 设置当前显示页
		pm.setCurpageindex(pageindex);
		// 设置分页信息
		request.setAttribute("pagermetal", pm);
		// 分发请求参数
		dispatchParams(request, response);
		try {
			request.getRequestDispatcher("/admin/commentmanager.jsp").forward(
					request, response);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
