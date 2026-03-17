const { exec } = require('child_process');

console.log('🚀 启动长途拼车平台前端...');

// 安装依赖
console.log('📦 安装前端依赖...');
exec('cd frontend && npm install', (error, stdout, stderr) => {
  if (error) {
    console.error(`依赖安装失败: ${error}`);
    return;
  }
  
  console.log('✅ 依赖安装完成');
  
  // 启动前端开发服务器
  console.log('🌐 启动前端开发服务器...');
  const frontendProcess = exec('cd frontend && npm run dev', (error, stdout, stderr) => {
    if (error) {
      console.error(`前端启动失败: ${error}`);
      return;
    }
    console.log(stdout);
  });
  
  frontendProcess.stdout.on('data', (data) => {
    console.log(`前端: ${data}`);
  });
  
  frontendProcess.stderr.on('data', (data) => {
    console.error(`前端错误: ${data}`);
  });
});
