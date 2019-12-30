package com.eshop.config;

import javax.sql.DataSource;

import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.activerecord.generator.Generator;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.eshop.config.ProjectConfig;

/**
 * 在数据库表有任何变动时，运行一下 main 方法，极速响应变化进行代码重构
 */
public class _JFinalProjectGenerator {
	
	public static DataSource getDataSource() {
		ProjectConfig c = new ProjectConfig();
		C3p0Plugin cp = ProjectConfig.createC3p0Plugin();
		cp.start();
		return cp.getDataSource();
	}
	
	public static void main(String[] args) {
		// base model 所使用的包名
		String baseModelPackageName = "com.eshop.model.base";
		// base model 文件保存路径
		String baseModelOutputDir = PathKit.getWebRootPath() + "\\src\\main\\java\\com\\eshop\\model\\base";
		
		System.out.println(baseModelOutputDir);
		
		// model 所使用的包名 (MappingKit 默认使用的包名)
		String modelPackageName = "com.eshop.model";
		// model 文件保存路径 
		String modelOutputDir = PathKit.getWebRootPath() + "\\src\\main\\java\\com\\eshop\\model";
		
		// 创建生成器
		Generator gernerator = new Generator(getDataSource(), baseModelPackageName, baseModelOutputDir, modelPackageName, modelOutputDir);
		// 设置数据库方言
		gernerator.setDialect(new MysqlDialect());
		// 添加不需要生成的表名
		gernerator.addExcludedTable("migrations");
		// 设置是否在 Model 中生成 dao 对象
		gernerator.setGenerateDaoInModel(true);
		// 设置是否生成字典文件
		gernerator.setGenerateDataDictionary(false);
		// 设置需要被移除的表名前缀用于生成modelName。例如表名 "osc_user"，移除前缀 "osc_"后生成的model名为 "User"而非 OscUser
		//gernerator.setRemovedTableNamePrefixes("t_");
		// 生成
		gernerator.generate();
	}
}




