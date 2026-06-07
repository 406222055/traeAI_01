import { useEffect, useState } from 'react';
import { Button, Card, Drawer, Form, Input, Select, Space, Table, Tag, Typography, message } from 'antd';
import type { Vendor } from '../../shared';
import { VENDOR_STATUSES } from '../../shared';
import { createVendor, fetchVendors, updateVendor } from '../../services/vendors';

export function VendorsPage() {
  const [items, setItems] = useState<Vendor[]>([]);
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState<Vendor | null>(null);
  const [form] = Form.useForm();

  const load = async () => {
    try {
      setItems(await fetchVendors());
    } catch (error) {
      message.error(error instanceof Error ? error.message : '加载服务商失败');
    }
  };

  useEffect(() => {
    void load();
  }, []);

  return (
    <Card
      title="服务商管理"
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
          新增服务商
        </Button>
      }
    >
      <Table
        rowKey="id"
        dataSource={items}
        columns={[
          { title: '名称', dataIndex: 'name' },
          { title: '统一社会信用代码', dataIndex: 'creditCode' },
          { title: '服务类别', dataIndex: 'serviceType' },
          { title: '联系人', dataIndex: 'contactName' },
          { title: '联系电话', dataIndex: 'contactPhone' },
          { title: '状态', dataIndex: 'status', render: (value: string) => <Tag color={value === 'active' ? 'green' : 'default'}>{value}</Tag> },
          { title: '备注', dataIndex: 'remark', render: (value: string | null) => value || '-' },
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
      <Drawer
        title={editing ? '编辑服务商' : '新增服务商'}
        open={open}
        onClose={() => setOpen(false)}
        width={480}
      >
        <Typography.Paragraph type="secondary">支持新增、编辑和详情级信息展示。</Typography.Paragraph>
        <Form
          layout="vertical"
          form={form}
          onFinish={async (values) => {
            try {
              if (editing) {
                await updateVendor(editing.id, values);
                message.success('服务商已更新');
              } else {
                await createVendor(values);
                message.success('服务商已创建');
              }
              setOpen(false);
              form.resetFields();
              await load();
            } catch (error) {
              message.error(error instanceof Error ? error.message : '保存服务商失败');
            }
          }}
        >
          <Form.Item label="名称" name="name" rules={[{ required: true }]}><Input /></Form.Item>
          <Form.Item label="统一社会信用代码" name="creditCode" rules={[{ required: true }]}><Input /></Form.Item>
          <Form.Item label="服务类别" name="serviceType" rules={[{ required: true }]}><Input /></Form.Item>
          <Form.Item label="联系人" name="contactName" rules={[{ required: true }]}><Input /></Form.Item>
          <Form.Item label="联系电话" name="contactPhone" rules={[{ required: true }]}><Input /></Form.Item>
          <Form.Item label="状态" name="status" rules={[{ required: true }]}>
            <Select options={VENDOR_STATUSES.map((value) => ({ label: value, value }))} />
          </Form.Item>
          <Form.Item label="备注" name="remark"><Input.TextArea rows={4} /></Form.Item>
          <Button type="primary" htmlType="submit" block>保存</Button>
        </Form>
      </Drawer>
    </Card>
  );
}
