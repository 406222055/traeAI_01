import { useEffect, useState } from 'react';
import { Button, Card, Drawer, Form, Input, Select, Space, Table, Tag, message } from 'antd';
import type { Project } from '../../shared';
import { PROJECT_STATUSES } from '../../shared';
import { createProject, fetchProjects, updateProject } from '../../services/projects';

export function ProjectsPage() {
  const [items, setItems] = useState<Project[]>([]);
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState<Project | null>(null);
  const [form] = Form.useForm();

  const load = async () => {
    try {
      setItems(await fetchProjects());
    } catch (error) {
      message.error(error instanceof Error ? error.message : '加载项目失败');
    }
  };

  useEffect(() => {
    void load();
  }, []);

  return (
    <Card
      title="项目管理"
      extra={
        <Button
          type="primary"
          onClick={() => {
            setEditing(null);
            form.resetFields();
            form.setFieldsValue({ status: 'active' });
            setOpen(true);
          }}
        >
          新增项目
        </Button>
      }
    >
      <Table
        rowKey="id"
        dataSource={items}
        columns={[
          { title: '项目编码', dataIndex: 'code' },
          { title: '项目名称', dataIndex: 'name' },
          { title: '区域', dataIndex: 'region' },
          { title: '负责人', dataIndex: 'managerName' },
          { title: '状态', dataIndex: 'status', render: (value: string) => <Tag color={value === 'active' ? 'green' : 'default'}>{value}</Tag> },
          {
            title: '操作',
            render: (_, record) => (
              <Space>
                <Button
                  onClick={() => {
                    setEditing(record);
                    form.setFieldsValue(record);
                    setOpen(true);
                  }}
                >
                  编辑
                </Button>
              </Space>
            ),
          },
        ]}
      />
      <Drawer title={editing ? '编辑项目' : '新增项目'} open={open} onClose={() => setOpen(false)} width={480}>
        <Form
          layout="vertical"
          form={form}
          onFinish={async (values) => {
            try {
              if (editing) {
                await updateProject(editing.id, values);
                message.success('项目已更新');
              } else {
                await createProject(values);
                message.success('项目已创建');
              }
              setOpen(false);
              form.resetFields();
              await load();
            } catch (error) {
              message.error(error instanceof Error ? error.message : '保存项目失败');
            }
          }}
        >
          <Form.Item label="项目编码" name="code" rules={[{ required: true }]}><Input /></Form.Item>
          <Form.Item label="项目名称" name="name" rules={[{ required: true }]}><Input /></Form.Item>
          <Form.Item label="区域" name="region" rules={[{ required: true }]}><Input /></Form.Item>
          <Form.Item label="负责人" name="managerName" rules={[{ required: true }]}><Input /></Form.Item>
          <Form.Item label="状态" name="status" rules={[{ required: true }]}>
            <Select options={PROJECT_STATUSES.map((value) => ({ label: value, value }))} />
          </Form.Item>
          <Button type="primary" htmlType="submit" block>保存</Button>
        </Form>
      </Drawer>
    </Card>
  );
}
