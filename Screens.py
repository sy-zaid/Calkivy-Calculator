screens = '''
ScreenManager:
    LoadingScreen:
    MainScreen:
    SimpleCalculator:
    
    Maths:
    APlusB:
    Factorial:
    GCD:
    
    Physics:
    
    Binary:
    
    
    
<LoadingScreen>:
    name:'LoadingScreen'
    
    orientation: 'vertical'
    spacing:5
    Image:
        source:'CALKIVY_Logo.png'
        size:self.size
        pos_hint:{'center_x':0.5,'center_y':0.6}
        
    
    Image:
        source: 'Loadingicon.gif'
        anim_delay:0.03
        anim_reset:True
        pos_hint:{'center_x':0.5,'center_y':0.19}
        size: self.size
    
            
        
<MainScreen>:
    name:'CALKIVY'
    
    MDBoxLayout: 
        orientation: "vertical"
        padding:[0,0,0,10]
        
        MDBoxLayout:
            orientation:'vertical'
            
            canvas.before:
                Rectangle:
                    pos:self.pos
                    size: self.size
                    source:'Main_Screen_BGimage.png'
            MDLabel:
                text: '[size=35][font=Caveat-VariableFont_wght]Welcome To Calkivy'
                markup:True
                text_size:self.size
                valign:'center'
                halign:'center'
                 
             
        MDBoxLayout:
            orientation:'vertical'
            spacing:20
            
            MDBoxLayout:
                orientation:'vertical'
                
                MDBoxLayout:
                    orientation:'horizontal'
                    spacing:10
                    padding:[20,10,20,10]  
                    
                    MDRectangleFlatButton:
                        text:'[size=17]Calculator'
                        theme_text_color: 'Custom'
                        
                        pos_hint:{'center_x':0.5}
    
                        size_hint_x:80
                        size_hint_y:2
                        on_press: 
                            root.manager.current = 'Calculator'
                            root.manager.transition.direction='left'
                            
                    MDRectangleFlatButton:
                        text:'[size=17]Algebra'
                        theme_text_color: 'Custom'
                        
                        pos_hint:{'center_x':0.5}
                    
                        size_hint_x:80
                        size_hint_y:2
                        on_press: 
                            root.manager.current = 'Mathematics'
                            root.manager.transition.direction='left'
                
                MDBoxLayout:
                    orientation:'horizontal'
                    spacing:10         
                    padding:[20,10,20,10]  
                    MDRectangleFlatButton:
                        text:'[size=17]Physics'
                        pos_hint:{'center_x':0.5,'center_y':0.1}
                        size_hint_x:80
                        size_hint_y:2
                        
                        on_press:
                            root.manager.current = 'Physics'
                            root.manager.transition.direction='left'
                            
                    MDRectangleFlatButton:
                        text:'[size=17]Binary'
                        pos_hint:{'center_x':0.5,'center_y':0.1}
                        size_hint_x:80
                        size_hint_y:2
                        on_press:
                            root.manager.current = 'Binary'
                            root.manager.transition.direction='left'
                        
            MDBoxLayout:
                orientation:'vertical'
                padding:10
                MDFloatingActionButton:
                    MDIcon:
                        icon:'github-icon.png'
                        font_size:39   

    
<SimpleCalculator>:
    name:'Calculator'
    
    MDBoxLayout:
        orientation:'vertical'
        padding:[10,5,10,20]
        spacing:10
        theme_text_color: 'Custom'
        
        canvas.before:
            Color:
                rgba: (1, 1, 1, 1)
            Rectangle:
                pos:self.pos
                size: self.size
                source:'Main_Screen_BGimage.png'  
                
        MDLabel:
            text:'[size=30][font=Caveat-VariableFont_wght]Maths Calculator'
            markup:True
            text_size:self.size
            halign:'center'
            valign:'center'
            pos_hint: {'center_x':0.5,'center_y':1}
        
        TextInput:
            id:Text_Input_Calculator
            padding:[10,5,10,0]
            hint_text: "0"
            font_name:'RedHatDisplay-Medium'
            text_size:self.size
            halign:'right'
            valign:'bottom'
            theme_text_color: 'Custom'
            background_color: (1,1,1,0.7)
            
            pos_hint: {'center_x':0.5}
            
            
        MDGridLayout:
            rows:5
            cols:4
            row_force_default:True
            row_default_height:70
            spacing:5
            
            MDRectangleFlatButton:
                id:power
                text:'x²'
                md_bg_color: 1, 1, 1, 0.9
                theme_text_color :'Custom'
                size_hint:(100,1)
                on_press : root.power()
            MDRectangleFlatButton:
                id:clear
                text:'[size=18]C'
                markup:True
                text_color:'white'
                md_bg_color:136/255, 9/255, 66/255 ,0.5
                size_hint:(100,1)
                on_press:root.clr()
                
            MDRectangleFlatButton:
                id:delete
                text:'\u232b'
                
                font_name:  "seguisym"
                md_bg_color: 1, 1, 1, 0.9
                theme_text_color :'Custom'
                size_hint:(100,1)
                on_press:root.delete()
                
            MDRectangleFlatButton:
                id:divide
                text:'[size=18]\u00f7'
                markup:True
                md_bg_color: 1, 1, 1, 0.9
                theme_text_color :'Custom'
                size_hint:(100,1)
                on_press : root.add_value('/')
            MDRectangleFlatButton:
                id:seven
                text:'7'
                md_bg_color: 1, 1, 1, 0.7
                theme_text_color :'Custom'
                size_hint:(100,1)
                on_press : root.add_value(self.text)
            MDRectangleFlatButton:
                id:eight
                text:'8'
                md_bg_color: 1, 1, 1, 0.7
                theme_text_color :'Custom'
                size_hint:(100,1)
                on_press : root.add_value(self.text)
            MDRectangleFlatButton:
                id:nine
                text:'9'
                md_bg_color: 1, 1, 1, 0.7
                theme_text_color :'Custom'
                size_hint:(100,1)
                on_press : root.add_value(self.text)
            MDRectangleFlatButton:
                id:multiply
                text:'[size=18]x'
                markup:True
                md_bg_color: 1, 1, 1, 0.9
                theme_text_color :'Custom'
                size_hint:(100,1)
                on_press : root.add_value('*')
            MDRectangleFlatButton:
                id:four
                text:'4'
                md_bg_color: 1, 1, 1, 0.7
                theme_text_color :'Custom'
                size_hint:(100,1)
                on_press : root.add_value(self.text)
            MDRectangleFlatButton:
                id:five
                text:'5'
                md_bg_color: 1, 1, 1, 0.7
                theme_text_color :'Custom'
                size_hint:(100,1)
                on_press : root.add_value(self.text)
            MDRectangleFlatButton:
                id:six
                text:'6'
                md_bg_color: 1, 1, 1, 0.7
                theme_text_color :'Custom'
                size_hint:(100,1)
                on_press : root.add_value(self.text)
            MDRectangleFlatButton:
                id:plus
                text:'[size=18]+'
                markup:True
                md_bg_color: 1, 1, 1, 0.9
                theme_text_color :'Custom'
                size_hint:(100,1)
                on_press : root.add_value('+')
            MDRectangleFlatButton:
                id:one
                text:'1'
                md_bg_color: 1, 1, 1, 0.7
                theme_text_color :'Custom'
                size_hint:(100,1)
                on_press : root.add_value(self.text)
            MDRectangleFlatButton:
                id:two
                text:'2'
                md_bg_color: 1, 1, 1, 0.7
                theme_text_color :'Custom'
                size_hint:(100,1)
                on_press : root.add_value(self.text)
            MDRectangleFlatButton:
                id:three
                text:'3'
                md_bg_color: 1, 1, 1, 0.7
                theme_text_color :'Custom'
                size_hint:(100,1)
                on_press : root.add_value(self.text)
            MDRectangleFlatButton:
                id:minus
                text:'[size=18]-'
                markup:True
                md_bg_color: 1, 1, 1, 0.9
                theme_text_color :'Custom'
                size_hint:(100,1)
                on_press : root.add_value('-')
            MDRectangleFlatButton:
                id:pos_neg
                text:'[size=18]+/-'
                markup:True
                md_bg_color: 1, 1, 1, 0.7
                theme_text_color :'Custom'
                size_hint:(100,1)
                on_press:root.pos_neg()
            MDRectangleFlatButton:
                id:zero
                text:'0'
                md_bg_color: 1, 1, 1, 0.7
                theme_text_color :'Custom'
                size_hint:(100,1)
                on_press : root.add_value(self.text)
            MDRectangleFlatButton:
                id:decimal
                text:'[size=30].'
                markup:True
                md_bg_color: 1, 1, 1, 0.7
                theme_text_color :'Custom'
                size_hint:(100,1)
                on_press : root.add_value('.')
            MDRectangleFlatButton:
                text:'='
                text_color:'white'
                md_bg_color:136/255, 9/255, 66/255 ,0.5
                size_hint:(100,1)
                on_press: root.equals()
        MDLabel:
            size_hint:(None,None)
            height:300
        MDRectangleFlatButton:
            text:'Back'
            pos_hint: {'center_x':0.5,'center_y':1}
            size_hint:(None,None)
            height:150
            width:100
            on_press:
                root.manager.transition.direction = 'right'
                root.manager.current = 'CALKIVY'
        
                                  
                              
<Maths>:
    name:'Mathematics'
    MDBoxLayout:
        orientation:'vertical'
        padding: [5,5,5,135]
        
        theme_text_color: 'Custom'
        
        canvas.before:
            Color:
                rgba: (1, 1, 1, 1)
            Rectangle:
                pos:self.pos
                size: self.size
                source:'Main_Screen_BGimage.png' 
    
        MDLabel:
            text:'[size=30][font=Caveat-VariableFont_wght]Mathematics'
            markup:True
            text_size:self.size
            halign:'center'
            valign:'center'
            
        MDBoxLayout:
            orientation:'vertical'
            spacing:30
            padding:[40,100,40,100] 
            
            MDRectangleFlatButton:
                id:aplusb
                text:'[size=16]( a ± b )²'
                font_name:  "seguisym"
                md_bg_color: 1, 1, 1, 0.9
                theme_text_color :'Custom'
                size_hint_x:0.6
                pos_hint: {'center_x':0.5,'center_y':0.5}
                
                on_press: 
                    root.manager.transition.direction='left'
                    root.manager.current = 'aplusb'   
                
            MDRectangleFlatButton:
                id:gcd
                text:'[size=16]GCD'
                font_name:  "seguisym"
                md_bg_color: 1, 1, 1, 0.9
                theme_text_color :'Custom'
                size_hint_x:0.6
                pos_hint: {'center_x':0.5,'center_y':0.5}
                
                on_press: 
                    root.manager.transition.direction='left'
                    root.manager.current = 'GCD'   
                 
            MDRectangleFlatButton:
                id:Factorial
                text:'[size=16]Factorial'
                font_name:  "seguisym"
                md_bg_color: 1, 1, 1, 0.9
                theme_text_color :'Custom'
                pos_hint: {'center_x':0.5,'center_y':0.5}
                size_hint_x:0.6
                
                on_press: 
                    root.manager.transition.direction='left'
                    root.manager.current = 'factorial'    
               
        MDRectangleFlatButton:
            text:'[size=16]Back'
            text_size:self.text
            valign:'bottom'
            pos_hint: {'center_x':0.5}
            size_hint_x:0.3
            
            on_press:
                root.manager.transition.direction = 'right'
                root.manager.current = 'CALKIVY'
                
        
<APlusB>:
    name:'aplusb'
    MDBoxLayout:
        orientation:'vertical'
        padding:5
        spacing:10
        theme_text_color: 'Custom'
        
        canvas.before:
            Color:
                rgba: (1, 1, 1, 1)
            Rectangle:
                pos:self.pos
                size: self.size
                source:'Main_Screen_BGimage.png'  
                
        MDLabel:
            text:'[size=30][font=Caveat-VariableFont_wght]( a ± b )²'
            markup:True
            text_size:self.size
            halign:'center'
            valign:'center'
            pos_hint: {'center_x':0.5,'center_y':1}
        
        TextInput:
            id:Text_Input_1
            padding:[10,5,10,0]
            hint_text: "Value of a & b only. Use '^' with value for exponent."
            font_name:'RedHatDisplay-Medium'
            text_size:self.size
            halign:'right'
            valign:'bottom'
            theme_text_color: 'Custom'
            background_color: (1,1,1,0.7)
            
            pos_hint: {'center_x':0.5}    
        
        MDBoxLayout:
            orientation:'horizontal'
            spacing:10
            padding:[50,0,50,10]
            MDRectangleFlatButton:
                id:square
                text:'[size=16]x²'
                
                font_name:  "seguisym"
                md_bg_color: 1, 1, 1, 0.9
                theme_text_color :'Custom'
                size_hint:(75,0.5)
                on_press:root.power_buttons(self.text)    
                
            MDRectangleFlatButton:
                id:cube
                text:'[size=16]x³'
                font_name:  "seguisym"
                md_bg_color: 1, 1, 1, 0.9
                theme_text_color :'Custom'
                size_hint:(75,0.5)
                on_press:root.power_buttons(self.text)    
                 
            MDRectangleFlatButton:
                id:clr
                text:'[size=16]Clear'
                markup:True
                font_name:  "seguisym"
                md_bg_color: 1, 1, 1, 0.9
                theme_text_color :'Custom'
                size_hint:(75,0.5)
                on_press:root.power_buttons(self.text) 
            
        MDRectangleFlatButton:
            id:calculate
            text:'[size=16]Calculate'
            markup:True
            font_name:  "seguisym"
            md_bg_color: 1, 1, 1, 0.9
            pos_hint: {'center_x':0.5,'center_y':0.5}
            theme_text_color :'Custom'
            size_hint:(0.5,None)
            on_press: root.update(), root.ab_wholesq(root.ids.Text_Input_1.text)  
                      
        MDLabel:
            id: answer
            text: 'Your Answer'
            font_name:'RedHatDisplay-Medium'
            markup:True
            pos_hint: {'center_x':0.5}
            size_hint:(None,None)
            height:100
            width:150            
            halign:'center'      
               
        MDRectangleFlatButton:
            text:'[size=16]Back'
            text_size:self.text
            valign:'bottom'
            pos_hint: {'center_x':0.5}
            size_hint_x:0.3
            
            on_press:
                root.manager.transition.direction = 'right'
                root.manager.current = 'Mathematics'
        MDLabel:
                    
<Factorial>:
    name:'factorial'
    MDBoxLayout:
        orientation:'vertical'
        padding:5
        spacing:10
        theme_text_color: 'Custom'
        
        canvas.before:
            Color:
                rgba: (1, 1, 1, 1)
            Rectangle:
                pos:self.pos
                size: self.size
                source:'Main_Screen_BGimage.png'  
                
        MDLabel:
            text:'[size=30][font=Caveat-VariableFont_wght]Factorial'
            markup:True
            text_size:self.size
            halign:'center'
            valign:'center'
            pos_hint: {'center_x':0.5,'center_y':1}
        
        TextInput:
            id:Text_Input_Factorial
            padding:[10,5,10,0]
            hint_text: "Number to find factorial."
            font_name:'RedHatDisplay-Medium'
            text_size:self.size
            halign:'right'
            valign:'bottom'
            theme_text_color: 'Custom'
            background_color: (1,1,1,0.7)
            
            pos_hint: {'center_x':0.5}    
           
        MDBoxLayout: 
            orientation:'vertical'
            padding:[0,0,0,0]
            spacing:10         
            MDRectangleFlatButton:
                id:clr
                text:'[size=16]Clear'
                markup:True
                font_name:  "seguisym"
                md_bg_color: 1, 1, 1, 0.9
                theme_text_color :'Custom'
                pos_hint: {'center_x':0.5}
                size_hint:(0.5,None)
                on_press:root.clr(root.ids.Text_Input_Factorial) 
                
            MDRectangleFlatButton:
                id:calculate
                text:'[size=16]Calculate'
                markup:True
                font_name:  "seguisym"
                md_bg_color: 1, 1, 1, 0.9
                pos_hint: {'center_x':0.5,'center_y':0.5}
                theme_text_color :'Custom'
                size_hint:(0.5,None)
                on_press: root.factorial(root.ids.Text_Input_Factorial,root.ids.answer)
                          
        MDLabel:
            id: answer
            text: 'Your Answer'
            font_name:'RedHatDisplay-Medium'
            markup:True
            pos_hint: {'center_x':0.5}
            size_hint:(None,None)
            height:100
            width:150            
            halign:'center'      
               
        MDRectangleFlatButton:
            text:'[size=16]Back'
            text_size:self.text
            valign:'bottom'
            pos_hint: {'center_x':0.5}
            size_hint_x:0.3
            
            on_press:
                root.manager.transition.direction = 'right'
                root.manager.current = 'Mathematics'
        MDLabel:
        
        
<GCD>:
    name:'GCD'
    MDBoxLayout:
        orientation:'vertical'
        padding:5
        spacing:10
        theme_text_color: 'Custom'
        
        canvas.before:
            Color:
                rgba: (1, 1, 1, 1)
            Rectangle:
                pos:self.pos
                size: self.size
                source:'Main_Screen_BGimage.png'  
                
        MDLabel:
            text:'[size=30][font=Caveat-VariableFont_wght]Greatest Common Divisor'
            markup:True
            text_size:self.size
            halign:'center'
            valign:'center'
            pos_hint: {'center_x':0.5,'center_y':1}
        
        TextInput:
            id:Text_Input_GCD
            padding:[10,5,10,0]
            hint_text: "Enter 2 numbers e.g. 2,4"
            font_name:'RedHatDisplay-Medium'
            text_size:self.size
            halign:'right'
            valign:'bottom'
            theme_text_color: 'Custom'
            background_color: (1,1,1,0.7)
            
            pos_hint: {'center_x':0.5}    
           
        MDBoxLayout: 
            orientation:'vertical'
            padding:[0,0,0,0]
            spacing:10    
            MDRectangleFlatButton:
                id:clr
                text:'[size=16]Clear'
                markup:True
                font_name:  "seguisym"
                md_bg_color: 1, 1, 1, 0.9
                theme_text_color :'Custom'
                pos_hint: {'center_x':0.5}
                size_hint:(0.5,None)
                on_press:root.clr(root.ids.Text_Input_GCD) 
                
            MDRectangleFlatButton:
                id:calculate
                text:'[size=16]Calculate'
                markup:True
                font_name:  "seguisym"
                md_bg_color: 1, 1, 1, 0.9
                pos_hint: {'center_x':0.5,'center_y':0.5}
                theme_text_color :'Custom'
                size_hint:(0.5,None)
                on_press: root.gcd(root.ids.Text_Input_GCD,root.ids.answer)
                          
        MDLabel:
            id: answer
            text: 'Your Answer'
            font_name:'RedHatDisplay-Medium'
            markup:True
            pos_hint: {'center_x':0.5}
            size_hint:(None,None)
            height:100
            width:150            
            halign:'center'      
               
        MDRectangleFlatButton:
            text:'[size=16]Back'
            text_size:self.text
            valign:'bottom'
            pos_hint: {'center_x':0.5}
            size_hint_x:0.3
            
            on_press:
                root.manager.transition.direction = 'right'
                root.manager.current = 'Mathematics'
        MDLabel:
           
           
           
<Physics>:
    name:'Physics'
    MDBoxLayout:
        orientation:'vertical'
        padding:[30,30,30,60]
        canvas.before:
            Color:
                rgba: (1, 1, 1, 1)
            Rectangle:
                pos:self.pos
                size: self.size
                source:'Main_Screen_BGimage.png' 
            
    ScrollView:
        do_scroll_x: False
        do_scroll_y: True
          
        MDGridLayout:
            
            cols:1
            size_hint_y: None
            # height: self.minimum_height
            padding:[30,50,30,0]
            MDLabel:
                text:'[size=30][font=Caveat-VariableFont_wght]Physics'
                markup:True
                text_size:self.size
                halign:'center'
                valign:'top'
                size_hint:(0.5,None)
                height:60
                pos_hint: {'center_x':0.5}    
            
            MDLabel:
                text:'[size=18]Length.'
                font_name:'RedHatDisplay-Medium'
                markup:True
                size_hint:(None,None)
                height:70
                width:150            
                halign:'left'
                
            MDGridLayout:
                
                rows:1
                cols:2
                spacing:5
                
                TextInput:
                    id:Text_Input_Length
                    padding:[10,5,10,0]
                    hint_text: "Enter Value for Conversion"
                    font_name:'RedHatDisplay-Medium'
                    text_size:self.size
                    halign:'right'
                    valign:'bottom'
                    size_hint:(0.7,None)
                    height:50
                    theme_text_color: 'Custom'
                    background_color: (1,1,1,0.7)
                    
                Spinner:
                    text:'Unit'
                    values:['Km','m'] 
                    size_hint:(0.2,None)
                    height:50
                    theme_text_color: 'Custom'
                    background_normal : ''
                    background_color : [136/255, 9/255, 66/255 , 0.5]
                    
                MDRectangleFlatButton:
                    text:'[size=16]Back'
                    text_size:self.text
                    valign:'top'
                    halign:'center'
                    
                    size_hint:(0.2,None)
                    height:50
                    
                    on_press:
                        root.manager.transition.direction = 'right'
                        root.manager.current = 'CALKIVY'        
                
        
<Binary>:
    name:'Binary'
    MDLabel:
        text: "[size=30]Enter Binary"
        markup:True
        pos_hint: {'center_x':0.5,'center_y':0.9}
        size_hint_x:None
        width:300
        
    MDRectangleFlatButton:
        text:'Calculate'
        pos_hint: {'center_x':0.5,'center_y':0.5}
    MDRectangleFlatButton:
        text:'[size=16]Back'
        text_size:self.text
        valign:'bottom'
        pos_hint: {'center_x':0.5}
        size_hint_x:0.3
        
        on_press:
            root.manager.transition.direction = 'right'
            root.manager.current = 'CALKIVY'
                
'''



