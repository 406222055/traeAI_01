import { Layout, Menu, Space, Typography, Button } from 'antd';
import type { MenuProps } from 'antd';
import { Link, Outlet, useLocation, useNavigate } from 'react-router-dom';
import { clearAuth, getStoredUser } from '../utils/auth';

const { Header, Content, Sider } = Layout;

const menuItems: MenuProps['items'] = [
  { key: '/', label: <Link to="/">首页</Link> },
  { key: '/vendors', label: <Link to="/vendors">服务商管理</Link> },
  { key: '/projects', label: <Link to="/projects">项目管理</Link> },
  { key: '/admissions', label: <Link to="/admissions">准入申请</Link> },
  { key: '/certificates', label: <Link to="/certificates">人员证照</Link> },
  { key: '/alerts', label: <Link to="/alerts">到期预警</Link> },
];

export function AppLayout() {
  const location = useLocation();
  const navigate = useNavigate();
  const user = getStoredUser();

  const selectedKey =
    menuItems?.find((item) => typeof item?.key === 'string' && location.pathname.startsWith(String(item.key)))?.key ?? '/';

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider>
        <div style={{ color: '#fff', padding: '16px 20px', fontSize: 16, fontWeight: 600 }}>外协服务商管控平台</div>
        <Menu theme="dark" mode="inline" selectedKeys={[String(selectedKey)]} items={menuItems} />
      </Sider>
      <Layout>
        <Header style={{ background: '#fff', display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '0 24px' }}>
          <Typography.Title level={4} style={{ margin: 0 }}>
            工程外协服务商数字化管控平台
          </Typography.Title>
          <Space>
            <Typography.Text>{user?.name ?? '未登录'}</Typography.Text>
            <Button
              onClick={() => {
                clearAuth();
                navigate('/login');
              }}
            >
              退出登录
            </Button>
          </Space>
        </Header>
        <Content style={{ padding: 24 }}>
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
}
