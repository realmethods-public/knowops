#set( $className = $classObject.getName() )
#set( $lowercaseClassName = ${Utils.lowercaseFirstLetter(${className})} )
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { Create${className}Component } from './create.component';

describe('Create${className}Component', () => {
  let component: Create${className}Component;
  let fixture: ComponentFixture<Create${className}Component>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule],
      declarations: [ Create${className}Component ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(Create${className}Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
