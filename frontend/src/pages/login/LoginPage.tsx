import { useState } from 'react';
import { Button, Card, Form, Input, Layout, Typography, message } from 'antd';
import { useNavigate } from 'react-router-dom';
import { login } from '../../services/auth';
import { setAuth } from '../../utils/auth';

const demoAccounts = [
  'platform_admin / Admin123456',
  'project_admin / Project123456',
];

export function LoginPage() {
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  return (
    <Layout style={{ minHeight: '100vh', display: 'grid', placeItems: 'center', padding: 24 }}>
      <Card style={{ width: 420 }}>
        <Typography.Title level={3}>登录</Typography.Title>
        <Typography.Paragraph type="secondary">
          演示账号：{demoAccounts.join('；')}
        </Typography.Paragraph>
        <Form
          layout="vertical"
          onFinish={async (values) => {
            setLoading(true);
            try {
              const result = await login(values);
              setAuth(result.token, result.user);
              message.success('登录成功');
              navigate('/');
            } catch (error) {
              message.error(error instanceof Error ? error.message : '登录失败');
            } finally {
              setLoading(false);
            }
          }}
        >
          <Form.Item label="用户名" name="username" rules={[{ required: true, message: '请输入用户名' }]}>
            <Input placeholder="请输入用户名" />
          </Form.Item>
          <Form.Item label="密码" name="password" rules={[{ required: true, message: '请输入密码' }]}>
            <Input.Password placeholder="请输入密码" />
          </Form.Item>
          <Button type="primary" htmlType="submit" block loading={loading}>
            登录
          </Button>
        </Form>
      </Card>
    </Layout>
  );
}
