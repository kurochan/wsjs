package controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import play.api.templates.Html;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import views.html.index;
import views.html.result;
import scala.collection.mutable.StringBuilder;

public class Application extends Controller {

	public static Result index() {
		try {
			List<String> titles = getTitleList();
			return ok(index.render(titles));
		} catch (IOException e) {
			return internalServerError("Error: 設問一覧の取得に失敗しました。");
		}
	}

	public static Result problem(int id) {
		try {
			BufferedReader br = getBufferedReaderFromFile("problems/p"
					+ String.format("%04d", id) + "/problem.html");
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
			return ok(views.html.problem.render(id, new Html(sb)));
		} catch (IOException e) {
			e.printStackTrace();
			return notFound("指定された設問ファイルが存在しませんでした。");
		}
	}

	public static Result inputFile(int id) {
		response().setContentType("application/octet-stream");
		response().setHeader(
				"Content-Disposition",
				"attachment; filename=input" + String.format("%03d", id)
						+ ".txt");
		return ok(new File("problems/p" + String.format("%04d", id)
				+ "/input.txt"));
	}

	public static Result result() {
		if (flash("ac") == null && flash("wa") == null) {
			return redirect(routes.Application.index());
		}
		boolean res = flash("ac") != null;
		int id = Integer.parseInt(flash("id"));
		return ok(result.render(id, res));
	}

	public static Result upload(int id) {
		File input = getFileFromRequest();
		File answer = new File("problems/p" + String.format("%04d", id)
				+ "/answer.txt");
		try {
			flash("id", Integer.toString(id));
			if (isCollectAnswer(answer, input)) {
				flash("ac", "accepted");
			} else {
				flash("wa", "wrong answer");
			}
			return redirect(routes.Application.result());
		} catch (Exception e) {
			e.printStackTrace();
			flash("error", "Missing file");
			return redirect(routes.Application.index());
		}
	}

	private static List<String> getTitleList() throws IOException {
		List<String> titles = new ArrayList<String>();
		BufferedReader br = getBufferedReaderFromFile("problems/problem_list.txt");
		String line;
		while ((line = br.readLine()) != null) {
			titles.add(line);
		}
		return titles;
	}

	private static File getFileFromRequest() {
		MultipartFormData body = request().body().asMultipartFormData();
		FilePart input = body.getFile("answer");
		if (input != null) {
			// String fileName = input.getFilename();
			// String contentType = input.getContentType();
			return input.getFile();
		} else {
			return null;
		}
	}

	private static BufferedReader getBufferedReaderFromFile(String filename)
			throws FileNotFoundException {
		File file = new File(filename);
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(file)));
		return br;
	}

	@SuppressWarnings("resource")
	private static boolean isCollectAnswer(File input1, File input2)
			throws IOException {
		BufferedReader br1 = new BufferedReader(new FileReader(input1));
		BufferedReader br2 = new BufferedReader(new FileReader(input2));
		String line1, line2;
		while ((line1 = br1.readLine()) != null
				&& (line2 = br2.readLine()) != null) {
			if (!line1.equals(line2)) {
				return false;
			}
		}
		return true;
	}
}