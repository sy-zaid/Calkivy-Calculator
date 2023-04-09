from kivy.uix.screenmanager import Screen, ScreenManager

class SimpleCalculator(Screen):
    def add_value(self, obj):
        val = self.ids.Text_Input_Calculator.text
        if self.ids.Text_Input_Calculator.text == '0':
            self.ids.Text_Input_Calculator.text = self.ids.Text_Input_Calculator.text.replace('0', '', 1)
        for i in val:
            if i.isalpha():
                return self.clr()
                exit
        self.ids.Text_Input_Calculator.text = self.ids.Text_Input_Calculator.text + str(obj)

    def clr(self):
        self.ids.Text_Input_Calculator.text = '0'

    def delete(self):
        if self.ids.Text_Input_Calculator.text != '':
            val = self.ids.Text_Input_Calculator.text
            val_reduced = val[:-1]
            self.ids.Text_Input_Calculator.text = val_reduced

    def equals(self):
        try:
            res = str(eval(self.ids.Text_Input_Calculator.text))
            self.ids.Text_Input_Calculator.text = res
        except SyntaxError:
            self.ids.Text_Input_Calculator.text = 'Error! Syntax is incorrect.'
        except Exception:
            self.ids.Text_Input_Calculator.text = 'Error! Enter correct value please.'

    def power(self):
        try:
            val = self.ids.Text_Input_Calculator.text
            if val != '':
                val_powered = str(eval(val) ** 2)
                self.ids.Text_Input_Calculator.text = val_powered
        except BaseException:
            self.ids.Text_Input_Calculator.text = 'Error! Incorrect value'

    def pos_neg(self):
        try:
            self.ids.Text_Input_Calculator.text = str(
                eval(self.ids.Text_Input_Calculator.text) * -1)
        except BaseException:
            self.ids.Text_Input_Calculator.text = 'Enter correct value!'
