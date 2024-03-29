from tkinter import ttk, HORIZONTAL, VERTICAL, BOTTOM, RIGHT, X, Y


class Tree:
    def __init__(self, root):
        self.conn_tree = ttk.Treeview(master=root)

        # treeview超出屏幕的滚动条
        scroll_bar_tree_x = ttk.Scrollbar(master=self.conn_tree, orient=HORIZONTAL, command=self.conn_tree.xview)
        self.conn_tree.configure(xscrollcommand=scroll_bar_tree_x.set)
        scroll_bar_tree_x.pack(side=BOTTOM, fill=X)

        scroll_bar_tree_y = ttk.Scrollbar(master=self.conn_tree, orient=VERTICAL, command=self.conn_tree.yview)
        self.conn_tree.configure(yscrollcommand=scroll_bar_tree_y.set)
        scroll_bar_tree_y.pack(side=RIGHT, fill=Y)

        self.conn_tree.bind("<<TreeviewSelect>>", self.tree_item_clicked)

    def tree_item_clicked(self, event):
        for item in self.conn_tree.selection():
            print(item)

    def get_instance(self):
        return self.conn_tree
